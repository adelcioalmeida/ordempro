package br.com.ordempro.controller;

import br.com.ordempro.dto.OrdemServicoFormDTO;
import br.com.ordempro.model.Cliente;
import br.com.ordempro.model.ItemOrdemServico;
import br.com.ordempro.model.OrdemServico;
import br.com.ordempro.model.Servico;
import br.com.ordempro.model.Usuario;
import br.com.ordempro.service.CidadeService;
import br.com.ordempro.service.ClienteService;
import br.com.ordempro.service.EmailService;
import br.com.ordempro.service.ItemOrdemServicoService;
import br.com.ordempro.service.OrdemServicoService;
import br.com.ordempro.service.PdfService;
import br.com.ordempro.service.ServicoService;
import br.com.ordempro.service.UsuarioService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
public class OrdemServicoController {

    private static final String STATUS_ABERTA = "ABERTA";
    private static final String STATUS_FINALIZADA = "FINALIZADA";
    private static final String STATUS_CANCELADA = "CANCELADA";

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_GERENTE = "ROLE_GERENTE";
    private static final String ROLE_VENDEDOR = "ROLE_VENDEDOR";

    private final OrdemServicoService ordemServicoService;
    private final ClienteService clienteService;
    private final CidadeService cidadeService;
    private final ServicoService servicoService;
    private final ItemOrdemServicoService itemOrdemServicoService;
    private final UsuarioService usuarioService;
    private final PdfService pdfService;
    private final EmailService emailService;

    public OrdemServicoController(
            OrdemServicoService ordemServicoService,
            ClienteService clienteService,
            CidadeService cidadeService,
            ServicoService servicoService,
            ItemOrdemServicoService itemOrdemServicoService,
            UsuarioService usuarioService,
            PdfService pdfService,
            EmailService emailService
    ) {
        this.ordemServicoService = ordemServicoService;
        this.clienteService = clienteService;
        this.cidadeService = cidadeService;
        this.servicoService = servicoService;
        this.itemOrdemServicoService = itemOrdemServicoService;
        this.usuarioService = usuarioService;
        this.pdfService = pdfService;
        this.emailService = emailService;
    }

    @GetMapping("/ordens/nova")
    public String exibirFormularioNovaOrdem(
            @RequestParam(required = false) Long idCliente,
            Model model
    ) {
        OrdemServicoFormDTO form = new OrdemServicoFormDTO();
        form.setStatus(STATUS_ABERTA);
        form.setDataAbertura(LocalDateTime.now().toLocalDate());

        if (idCliente != null) {
            form.setIdCliente(idCliente);
            adicionarNomeClienteSelecionado(model, idCliente);
        }

        model.addAttribute("ordemForm", form);
        model.addAttribute("modoEdicao", false);

        return carregarFormulario(model);
    }

    @GetMapping("/ordens/editar/{id}")
    public String editarOrdem(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes,
            Authentication authentication
    ) {
        OrdemServico ordemServico = ordemServicoService.buscarComClientePorId(id);

        if (ordemServico == null) {
            redirectAttributes.addFlashAttribute("erro", "Ordem de serviço não encontrada.");
            return "redirect:/ordens";
        }

        if (usuarioEhVendedor(authentication) && !statusIgual(ordemServico, STATUS_ABERTA)) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Vendedor só pode editar ordens de serviço com status ABERTA."
            );
            return "redirect:/ordens";
        }

        ItemOrdemServico item = itemOrdemServicoService.buscarPrimeiroPorIdOrdem(id);

        OrdemServicoFormDTO form = new OrdemServicoFormDTO();
        form.setIdOs(ordemServico.getIdOs());
        form.setIdCliente(ordemServico.getCliente() != null ? ordemServico.getCliente().getIdCliente() : null);

        if (ordemServico.getDataAbertura() != null) {
            form.setDataAbertura(ordemServico.getDataAbertura().toLocalDate());
        }

        if (ordemServico.getDataPrevistaConclusao() != null) {
            form.setDataPrevistaConclusao(ordemServico.getDataPrevistaConclusao().toLocalDate());
        }

        form.setStatus(ordemServico.getStatus());
        form.setObservacao(ordemServico.getObservacao());
        form.setValor(formatarValorParaFormulario(ordemServico.getValorTotal()));

        if (item != null) {
            form.setIdItem(item.getIdItem());
            form.setIdServico(item.getServico() != null ? item.getServico().getIdServico() : null);
            form.setDescricao(item.getDescricao());
        }

        if (ordemServico.getCliente() != null) {
            model.addAttribute("nomeClienteSelecionado", ordemServico.getCliente().getNome());
        }

        model.addAttribute("ordemForm", form);
        model.addAttribute("modoEdicao", true);
        model.addAttribute("usuarioVendedor", usuarioEhVendedor(authentication));

        return carregarFormulario(model);
    }

    @PostMapping("/ordens/salvar")
    public String salvarOrdem(
            @ModelAttribute("ordemForm") OrdemServicoFormDTO form,
            Model model,
            RedirectAttributes redirectAttributes,
            Authentication authentication
    ) {
        if (camposObrigatoriosInvalidos(form)) {
            model.addAttribute("erro", "Preencha os campos obrigatórios: cliente e descrição.");
            model.addAttribute("modoEdicao", form.getIdOs() != null);
            model.addAttribute("ordemForm", form);
            model.addAttribute("usuarioVendedor", usuarioEhVendedor(authentication));
            adicionarNomeClienteSelecionado(model, form.getIdCliente());
            return carregarFormulario(model);
        }

        Cliente cliente = clienteService.buscarPorId(form.getIdCliente());
        Servico servico = form.getIdServico() != null ? servicoService.buscarPorId(form.getIdServico()) : null;
        Usuario usuario = usuarioService.buscarUsuarioLogado();

        if (cliente == null || usuario == null) {
            model.addAttribute("erro", "Cliente ou usuário inválido.");
            model.addAttribute("modoEdicao", form.getIdOs() != null);
            model.addAttribute("ordemForm", form);
            model.addAttribute("usuarioVendedor", usuarioEhVendedor(authentication));
            adicionarNomeClienteSelecionado(model, form.getIdCliente());
            return carregarFormulario(model);
        }

        BigDecimal valor;

        try {
            valor = converterValor(form.getValor());
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", "Valor inválido. Digite no formato 0,00.");
            model.addAttribute("modoEdicao", form.getIdOs() != null);
            model.addAttribute("ordemForm", form);
            model.addAttribute("usuarioVendedor", usuarioEhVendedor(authentication));
            adicionarNomeClienteSelecionado(model, form.getIdCliente());
            return carregarFormulario(model);
        }

        OrdemServico ordemServico = obterOuCriarOrdemServico(form);

        if (ordemServico.getIdOs() != null && usuarioEhVendedor(authentication)) {
            if (!statusIgual(ordemServico, STATUS_ABERTA)) {
                redirectAttributes.addFlashAttribute(
                        "erro",
                        "Vendedor só pode editar ordens de serviço com status ABERTA."
                );
                return "redirect:/ordens";
            }

            form.setStatus(ordemServico.getStatus());
        }

        ordemServico.setCliente(cliente);

        if (ordemServico.getIdOs() == null) {
            ordemServico.setUsuario(usuario);
            ordemServico.setEmailEnviado(false);
            ordemServico.setDataEnvioEmail(null);
            ordemServico.setStatus(STATUS_ABERTA);
        } else if (usuarioPodeAlterarStatus(authentication)) {
            ordemServico.setStatus(form.getStatus());
        }

        LocalDateTime dataAbertura;

        if (form.getDataAbertura() != null) {
            dataAbertura = form.getDataAbertura().atTime(LocalDateTime.now().toLocalTime());
        } else {
            dataAbertura = LocalDateTime.now();
        }

        ordemServico.setDataAbertura(dataAbertura);

        if (form.getDataPrevistaConclusao() != null) {
            ordemServico.setDataPrevistaConclusao(form.getDataPrevistaConclusao().atStartOfDay());
        } else {
            ordemServico.setDataPrevistaConclusao(null);
        }

        ordemServico.setObservacao(form.getObservacao());
        ordemServico.setValorTotal(valor);

        ordemServico = ordemServicoService.salvar(ordemServico);

        ItemOrdemServico item = obterOuCriarItem(form, ordemServico);
        item.setOrdemServico(ordemServico);
        item.setServico(servico);
        item.setDescricao(form.getDescricao());
        item.setValor(valor);
        itemOrdemServicoService.salvar(item);

        if (form.getIdOs() == null) {
            redirectAttributes.addFlashAttribute("sucesso", "Ordem de serviço cadastrada com sucesso.");
        } else {
            redirectAttributes.addFlashAttribute("sucesso", "Ordem de serviço atualizada com sucesso.");
        }

        return "redirect:/ordens";
    }

    @GetMapping("/ordens/cancelar/{id}")
    public String cancelarOrdem(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        OrdemServico ordemServico = ordemServicoService.buscarPorId(id);

        if (ordemServico == null) {
            redirectAttributes.addFlashAttribute("erro", "Ordem de serviço não encontrada.");
            return "redirect:/ordens";
        }

        if (statusIgual(ordemServico, STATUS_FINALIZADA)) {
            redirectAttributes.addFlashAttribute("erro", "Ordem finalizada não pode ser cancelada.");
            return "redirect:/ordens";
        }

        if (statusIgual(ordemServico, STATUS_CANCELADA)) {
            redirectAttributes.addFlashAttribute("erro", "Esta ordem já está cancelada.");
            return "redirect:/ordens";
        }

        ordemServico.setStatus(STATUS_CANCELADA);
        ordemServicoService.salvar(ordemServico);

        redirectAttributes.addFlashAttribute("sucesso", "Ordem de serviço cancelada com sucesso.");
        return "redirect:/ordens";
    }

    @GetMapping("/ordens")
    public String listarOrdens(
            @RequestParam(required = false) String termoBusca,
            @RequestParam(required = false) java.util.List<String> filtros,
            Model model
    ) {
        String status = null;
        String cliente = null;
        String servico = null;

        if (filtros != null && termoBusca != null && !termoBusca.isBlank()) {
            if (filtros.contains("status")) {
                status = termoBusca;
            }

            if (filtros.contains("cliente")) {
                cliente = termoBusca;
            }

            if (filtros.contains("servico")) {
                servico = termoBusca;
            }
        }

        boolean semFiltros =
                (filtros == null || filtros.isEmpty())
                        || termoBusca == null
                        || termoBusca.isBlank();

        java.util.List<OrdemServico> ordens;

        if (semFiltros) {
            ordens = ordemServicoService.listarUltimas5();
        } else {
            ordens = ordemServicoService.buscarComFiltros(status, cliente, servico);
        }

        model.addAttribute("ordens", ordens);
        model.addAttribute("totalOrdens", ordemServicoService.listarTodas().size());
        model.addAttribute("totalClientes", clienteService.listarTodos().size());
        model.addAttribute("ordensAbertas", ordemServicoService.buscarComFiltros("ABERTA", null, null).size());
        model.addAttribute("termoBusca", termoBusca);
        model.addAttribute("filtrosSelecionados", filtros);
        model.addAttribute("ordemService", ordemServicoService);

        return "ordens-lista";
    }

    @GetMapping("/ordens/pdf/{id}")
    public ResponseEntity<ByteArrayResource> gerarPdf(@PathVariable Long id) throws Exception {
        OrdemServico ordemServico = ordemServicoService.buscarComClientePorId(id);

        if (ordemServico == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] pdf = pdfService.gerarPdfOrdemServico(ordemServico);
        ByteArrayResource recursoPdf = new ByteArrayResource(pdf);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ordem-servico-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(recursoPdf);
    }

    @GetMapping("/ordens/email/{id}")
    public String enviarEmail(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) throws Exception {
        OrdemServico ordemServico = ordemServicoService.buscarComClientePorId(id);

        if (ordemServico == null) {
            redirectAttributes.addFlashAttribute("erro", "Ordem de serviço não encontrada.");
            return "redirect:/ordens";
        }

        byte[] pdf = pdfService.gerarPdfOrdemServico(ordemServico);

        emailService.enviarComPdf(
                "adelciopaty@gmail.com",
                pdf,
                ordemServico.getIdOs()
        );

        ordemServico.setEmailEnviado(true);
        ordemServico.setDataEnvioEmail(LocalDateTime.now());
        ordemServicoService.salvar(ordemServico);

        redirectAttributes.addFlashAttribute("sucesso", "E-mail enviado com sucesso.");
        return "redirect:/ordens";
    }

    private boolean camposObrigatoriosInvalidos(OrdemServicoFormDTO form) {
        return form.getIdCliente() == null ||
                form.getDescricao() == null ||
                form.getDescricao().isBlank();
    }

    private BigDecimal converterValor(String valorTexto) {
        if (valorTexto == null || valorTexto.isBlank()) {
            return BigDecimal.ZERO;
        }

        String valorLimpo = valorTexto.trim()
                .replace(".", "")
                .replace(",", ".");

        try {
            return new BigDecimal(valorLimpo);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor inválido.");
        }
    }

    private String formatarValorParaFormulario(BigDecimal valor) {
        if (valor == null) {
            return "";
        }

        return valor.toString().replace(".", ",");
    }

    private OrdemServico obterOuCriarOrdemServico(OrdemServicoFormDTO form) {
        if (form.getIdOs() != null) {
            OrdemServico existente = ordemServicoService.buscarPorId(form.getIdOs());

            if (existente != null) {
                return existente;
            }
        }

        return new OrdemServico();
    }

    private ItemOrdemServico obterOuCriarItem(
            OrdemServicoFormDTO form,
            OrdemServico ordemServico
    ) {
        if (form.getIdItem() != null) {
            ItemOrdemServico existente = itemOrdemServicoService.buscarPrimeiroPorIdOrdem(ordemServico.getIdOs());

            if (existente != null) {
                return existente;
            }
        }

        return new ItemOrdemServico();
    }

    private boolean statusIgual(OrdemServico ordemServico, String status) {
        return ordemServico.getStatus() != null &&
                ordemServico.getStatus().equalsIgnoreCase(status);
    }

    private boolean usuarioEhVendedor(Authentication authentication) {
        return usuarioTemPerfil(authentication, ROLE_VENDEDOR);
    }

    private boolean usuarioPodeAlterarStatus(Authentication authentication) {
        return usuarioTemPerfil(authentication, ROLE_ADMIN) ||
                usuarioTemPerfil(authentication, ROLE_GERENTE);
    }

    private boolean usuarioTemPerfil(Authentication authentication, String perfil) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> perfil.equals(authority.getAuthority()));
    }

    private void adicionarNomeClienteSelecionado(Model model, Long idCliente) {
        if (idCliente == null) {
            return;
        }

        Cliente cliente = clienteService.buscarPorId(idCliente);

        if (cliente != null) {
            model.addAttribute("nomeClienteSelecionado", cliente.getNome());
        }
    }

    private String carregarFormulario(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("clientesModal", clienteService.listarTodos());
        model.addAttribute("cidades", cidadeService.listarTodas());
        model.addAttribute("servicos", servicoService.listarTodos());

        return "ordem-servico";
    }
}