package br.com.ordempro.controller;

import br.com.ordempro.service.EmailService;
import br.com.ordempro.service.PdfService;

import br.com.ordempro.dto.OrdemServicoFormDTO;
import br.com.ordempro.model.Cliente;
import br.com.ordempro.model.ItemOrdemServico;
import br.com.ordempro.model.OrdemServico;
import br.com.ordempro.model.Servico;
import br.com.ordempro.model.Usuario;
import br.com.ordempro.service.ClienteService;
import br.com.ordempro.service.ItemOrdemServicoService;
import br.com.ordempro.service.OrdemServicoService;
import br.com.ordempro.service.ServicoService;
import br.com.ordempro.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;
    private final ClienteService clienteService;
    private final ServicoService servicoService;
    private final ItemOrdemServicoService itemOrdemServicoService;
    private final UsuarioService usuarioService;
    private final PdfService pdfService;
    private final EmailService emailService;

    public OrdemServicoController(OrdemServicoService ordemServicoService,
                                  ClienteService clienteService,
                                  ServicoService servicoService,
                                  ItemOrdemServicoService itemOrdemServicoService,
                                  UsuarioService usuarioService,
                                  PdfService pdfService,
                                  EmailService emailService) {
        this.ordemServicoService = ordemServicoService;
        this.clienteService = clienteService;
        this.servicoService = servicoService;
        this.itemOrdemServicoService = itemOrdemServicoService;
        this.usuarioService = usuarioService;
        this.pdfService = pdfService;
        this.emailService = emailService;
    }

    @GetMapping("/ordens/nova")
    public String novaOrdem(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("servicos", servicoService.listarTodos());
        model.addAttribute("ordemForm", new OrdemServicoFormDTO());
        return "ordem-servico";
    }

    @PostMapping("/ordens/salvar")
    public String salvarOrdem(@ModelAttribute("ordemForm") OrdemServicoFormDTO form, Model model) {

        Cliente cliente = clienteService.buscarPorId(form.getIdCliente());
        Servico servico = servicoService.buscarPorId(form.getIdServico());
        Usuario usuario = usuarioService.buscarPorId(1L); // temporário para teste

        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setCliente(cliente);
        ordemServico.setUsuario(usuario);
        ordemServico.setDataAbertura(form.getDataAbertura() != null ? form.getDataAbertura() : LocalDateTime.now());
        ordemServico.setDataPrevistaConclusao(form.getDataPrevistaConclusao());
        ordemServico.setStatus(form.getStatus());
        ordemServico.setObservacao(form.getObservacao());
        ordemServico.setValorTotal(form.getValor() != null ? form.getValor() : BigDecimal.ZERO);
        ordemServico.setEmailEnviado(false);
        ordemServico.setDataEnvioEmail(null);

        ordemServico = ordemServicoService.salvar(ordemServico);

        ItemOrdemServico item = new ItemOrdemServico();
        item.setOrdemServico(ordemServico);
        item.setServico(servico);
        item.setDescricao(form.getDescricao());
        item.setValor(form.getValor());

        itemOrdemServicoService.salvar(item);

        model.addAttribute("mensagem", "Ordem de serviço salva com sucesso!");
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("servicos", servicoService.listarTodos());
        model.addAttribute("ordemForm", new OrdemServicoFormDTO());
        return "ordem-servico";
    }

        @GetMapping("/ordens")
        public String listarOrdens(Model model){
            model.addAttribute("ordens", ordemServicoService.listarTodas());
            return "ordens-lista";
        }
    @GetMapping("/ordens/pdf/{id}")
    public ResponseEntity<ByteArrayResource> gerarPdf(@PathVariable Long id) throws Exception {
        OrdemServico ordemServico = ordemServicoService.buscarPorId(id);

        if (ordemServico == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] pdf = pdfService.gerarPdfOrdemServico(ordemServico);

        ByteArrayResource resource = new ByteArrayResource(pdf);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ordem-servico-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(resource);
    }
    @GetMapping("/ordens/email/{id}")
    public String enviarEmail(@PathVariable Long id) throws Exception {
        OrdemServico ordemServico = ordemServicoService.buscarPorId(id);

        if (ordemServico == null) {
            return "redirect:/ordens";
        }

        byte[] pdf = pdfService.gerarPdfOrdemServico(ordemServico);

        emailService.enviarComPdf(
                "adelciopaty@gmail.com",
                pdf,
                ordemServico.getIdOs()
        );

        ordemServico.setEmailEnviado(true);
        ordemServico.setDataEnvioEmail(java.time.LocalDateTime.now());
        ordemServicoService.salvar(ordemServico);

        return "redirect:/ordens";

    }
}






