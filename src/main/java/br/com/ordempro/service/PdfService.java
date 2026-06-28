package br.com.ordempro.service;

import br.com.ordempro.model.ItemOrdemServico;
import br.com.ordempro.model.OrdemServico;
import br.com.ordempro.repository.ItemOrdemServicoRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class PdfService {

    private static final Color AZUL_ESCURO = new Color(30, 86, 104);
    private static final Color CINZA_CLARO = new Color(245, 247, 250);
    private static final Color CINZA_BORDA = new Color(210, 218, 225);

    private final ItemOrdemServicoRepository itemOrdemServicoRepository;

    public PdfService(ItemOrdemServicoRepository itemOrdemServicoRepository) {
        this.itemOrdemServicoRepository = itemOrdemServicoRepository;
    }

    public byte[] gerarPdfOrdemServico(OrdemServico ordemServico) throws Exception {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            adicionarCabecalho(document, "ORDEM DE SERVIÇO", "Ficha detalhada da ordem cadastrada no sistema OrdemPro");
            adicionarResumoOrdem(document, ordemServico);
            adicionarDadosCliente(document, ordemServico);
            adicionarDadosServico(document, ordemServico);

            document.close();
            return out.toByteArray();

        } catch (DocumentException e) {
            throw new Exception("Erro ao gerar PDF da ordem de serviço.", e);
        }
    }

    public byte[] gerarRelatorioOrdens(List<OrdemServico> ordens) throws Exception {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            adicionarCabecalho(document, "RELATÓRIO DE ORDENS DE SERVIÇO", "Relatório gerado conforme os filtros aplicados no sistema OrdemPro");
            adicionarResumoRelatorio(document, ordens);

            if (ordens.isEmpty()) {
                Paragraph vazio = new Paragraph("Nenhuma ordem encontrada para os filtros informados.", fonteTexto());
                vazio.setSpacingBefore(14);
                document.add(vazio);
            } else {
                for (OrdemServico ordem : ordens) {
                    adicionarFichaOrdemRelatorio(document, ordem);
                }
            }

            document.close();
            return out.toByteArray();

        } catch (DocumentException e) {
            throw new Exception("Erro ao gerar relatório de ordens de serviço.", e);
        }
    }

    private void adicionarCabecalho(Document document, String titulo, String subtitulo) throws DocumentException {
        PdfPTable tabela = new PdfPTable(1);
        tabela.setWidthPercentage(100);

        PdfPCell celula = new PdfPCell();
        celula.setBackgroundColor(AZUL_ESCURO);
        celula.setPadding(14);
        celula.setBorder(Rectangle.NO_BORDER);

        Paragraph tituloParagrafo = new Paragraph(titulo, fonteTituloBranca());
        tituloParagrafo.setAlignment(Element.ALIGN_CENTER);

        Paragraph subtituloParagrafo = new Paragraph(subtitulo, fonteSubtituloBranca());
        subtituloParagrafo.setAlignment(Element.ALIGN_CENTER);
        subtituloParagrafo.setSpacingBefore(4);

        celula.addElement(tituloParagrafo);
        celula.addElement(subtituloParagrafo);

        tabela.addCell(celula);
        document.add(tabela);

        Paragraph data = new Paragraph("Emitido em: " + formatarData(LocalDateTime.now()), fonteTextoPequena());
        data.setAlignment(Element.ALIGN_RIGHT);
        data.setSpacingBefore(8);
        data.setSpacingAfter(14);

        document.add(data);
    }

    private void adicionarResumoRelatorio(Document document, List<OrdemServico> ordens) throws DocumentException {
        PdfPTable tabela = new PdfPTable(2);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[]{1f, 1f});
        tabela.setSpacingAfter(14);

        adicionarCardResumo(tabela, "TOTAL DE ORDENS", String.valueOf(ordens.size()));
        adicionarCardResumo(tabela, "VALOR TOTAL", formatarMoeda(calcularValorTotal(ordens)));

        document.add(tabela);
    }

    private void adicionarResumoOrdem(Document document, OrdemServico ordem) throws DocumentException {
        PdfPTable tabela = new PdfPTable(4);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[]{1f, 1.3f, 1.2f, 1.2f});
        tabela.setSpacingAfter(14);

        adicionarCardResumo(tabela, "ORDEM", "#" + valorTexto(ordem.getIdOs()));
        adicionarCardResumo(tabela, "STATUS", valorTexto(ordem.getStatus()));
        adicionarCardResumo(tabela, "ABERTURA", formatarData(ordem.getDataAbertura()));
        adicionarCardResumo(tabela, "VALOR", formatarMoeda(ordem.getValorTotal()));

        document.add(tabela);
    }

    private void adicionarFichaOrdemRelatorio(Document document, OrdemServico ordem) throws DocumentException {
        PdfPTable tabela = new PdfPTable(1);
        tabela.setWidthPercentage(100);
        tabela.setSpacingBefore(10);
        tabela.setSpacingAfter(12);

        PdfPCell cabecalho = new PdfPCell(new Phrase(
                "ORDEM #" + valorTexto(ordem.getIdOs()) + " - " + obterNomeCliente(ordem),
                fonteSecaoBranca()
        ));
        cabecalho.setBackgroundColor(AZUL_ESCURO);
        cabecalho.setPadding(8);
        cabecalho.setBorderColor(AZUL_ESCURO);
        tabela.addCell(cabecalho);

        PdfPCell corpo = new PdfPCell();
        corpo.setPadding(10);
        corpo.setBorderColor(CINZA_BORDA);
        corpo.setBackgroundColor(Color.WHITE);

        PdfPTable tabelaDatas = new PdfPTable(3);
        tabelaDatas.setWidthPercentage(100);
        tabelaDatas.setSpacingAfter(8);

        adicionarLinhaTabelaCompacta(tabelaDatas, "STATUS", valorTexto(ordem.getStatus()));
        adicionarLinhaTabelaCompacta(tabelaDatas, "ABERTURA", formatarData(ordem.getDataAbertura()));
        adicionarLinhaTabelaCompacta(tabelaDatas, "CONCLUSÃO", formatarData(ordem.getDataPrevistaConclusao()));

        corpo.addElement(tabelaDatas);

        corpo.addElement(criarLinha("Cliente", obterNomeCliente(ordem)));
        corpo.addElement(criarLinha("Serviço", obterPrimeiroServicoDaOrdem(ordem.getIdOs())));
        corpo.addElement(criarLinha("Valor total", formatarMoeda(ordem.getValorTotal())));
        corpo.addElement(criarLinha("Observação", valorTexto(ordem.getObservacao())));

        tabela.addCell(corpo);
        document.add(tabela);
    }

    private void adicionarDadosCliente(Document document, OrdemServico ordem) throws DocumentException {
        adicionarTituloSecao(document, "DADOS DO CLIENTE");

        PdfPTable tabela = new PdfPTable(2);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[]{1f, 2f});
        tabela.setSpacingAfter(14);

        adicionarLinhaTabela(tabela, "Cliente", obterNomeCliente(ordem));
        adicionarLinhaTabela(tabela, "Telefone", ordem.getCliente() != null ? valorTexto(ordem.getCliente().getTelefone()) : "NÃO INFORMADO");
        adicionarLinhaTabela(tabela, "Celular", ordem.getCliente() != null ? valorTexto(ordem.getCliente().getCelular()) : "NÃO INFORMADO");
        adicionarLinhaTabela(tabela, "E-mail", ordem.getCliente() != null ? valorTexto(ordem.getCliente().getEmail()) : "NÃO INFORMADO");
        adicionarLinhaTabela(tabela, "Endereço", obterEnderecoCliente(ordem));

        document.add(tabela);
    }

    private void adicionarDadosServico(Document document, OrdemServico ordem) throws DocumentException {
        adicionarTituloSecao(document, "DADOS DO SERVIÇO");

        PdfPTable tabela = new PdfPTable(2);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[]{1f, 2f});
        tabela.setSpacingAfter(14);

        adicionarLinhaTabela(tabela, "Status", valorTexto(ordem.getStatus()));
        adicionarLinhaTabela(tabela, "Data de abertura", formatarData(ordem.getDataAbertura()));
        adicionarLinhaTabela(tabela, "Previsão de conclusão", formatarData(ordem.getDataPrevistaConclusao()));
        adicionarLinhaTabela(tabela, "Valor total", formatarMoeda(ordem.getValorTotal()));
        adicionarLinhaTabela(tabela, "Observação", valorTexto(ordem.getObservacao()));

        document.add(tabela);
        adicionarItensDaOrdem(document, ordem);
    }

    private void adicionarItensDaOrdem(Document document, OrdemServico ordem) throws DocumentException {
        adicionarTituloSecao(document, "ITENS DA ORDEM");

        List<ItemOrdemServico> itens = itemOrdemServicoRepository.findByOrdemServico_IdOs(ordem.getIdOs());

        if (itens.isEmpty()) {
            Paragraph vazio = new Paragraph("Nenhum item cadastrado para esta ordem.", fonteTexto());
            vazio.setSpacingAfter(12);
            document.add(vazio);
            return;
        }

        PdfPTable tabela = new PdfPTable(3);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[]{1.4f, 2.5f, 1f});
        tabela.setSpacingAfter(12);

        adicionarCabecalhoTabela(tabela, "SERVIÇO");
        adicionarCabecalhoTabela(tabela, "DESCRIÇÃO");
        adicionarCabecalhoTabela(tabela, "VALOR");

        for (ItemOrdemServico item : itens) {
            adicionarCelulaTabela(tabela, obterNomeServico(item));
            adicionarCelulaTabela(tabela, valorTexto(item.getDescricao()));
            adicionarCelulaTabela(tabela, formatarMoeda(item.getValor()));
        }

        document.add(tabela);
    }

    private void adicionarTituloSecao(Document document, String titulo) throws DocumentException {
        Paragraph paragraph = new Paragraph(titulo, fonteSecao());
        paragraph.setSpacingBefore(8);
        paragraph.setSpacingAfter(6);
        document.add(paragraph);
    }

    private void adicionarCardResumo(PdfPTable tabela, String titulo, String valor) {
        PdfPCell celula = new PdfPCell();
        celula.setPadding(10);
        celula.setBorderColor(CINZA_BORDA);
        celula.setBackgroundColor(CINZA_CLARO);

        Paragraph tituloParagrafo = new Paragraph(titulo, fonteTextoPequenaNegrito());
        tituloParagrafo.setSpacingAfter(5);

        Paragraph valorParagrafo = new Paragraph(valor, fonteDestaque());

        celula.addElement(tituloParagrafo);
        celula.addElement(valorParagrafo);

        tabela.addCell(celula);
    }

    private Paragraph criarLinha(String titulo, String valor) {
        Paragraph paragraph = new Paragraph();
        paragraph.setSpacingAfter(4);
        paragraph.add(new Chunk(titulo + ": ", fonteTextoNegrito()));
        paragraph.add(new Chunk(valor, fonteTexto()));
        return paragraph;
    }

    private void adicionarLinhaTabela(PdfPTable tabela, String titulo, String valor) {
        PdfPCell celulaTitulo = new PdfPCell(new Phrase(titulo, fonteTextoNegrito()));
        celulaTitulo.setBackgroundColor(CINZA_CLARO);
        celulaTitulo.setPadding(7);
        celulaTitulo.setBorderColor(CINZA_BORDA);

        PdfPCell celulaValor = new PdfPCell(new Phrase(valor, fonteTexto()));
        celulaValor.setPadding(7);
        celulaValor.setBorderColor(CINZA_BORDA);

        tabela.addCell(celulaTitulo);
        tabela.addCell(celulaValor);
    }

    private void adicionarLinhaTabelaCompacta(PdfPTable tabela, String titulo, String valor) {
        PdfPCell celula = new PdfPCell();
        celula.setPadding(6);
        celula.setBorderColor(CINZA_BORDA);
        celula.setBackgroundColor(CINZA_CLARO);

        Paragraph tituloParagrafo = new Paragraph(titulo, fonteTextoPequenaNegrito());
        tituloParagrafo.setSpacingAfter(3);

        Paragraph valorParagrafo = new Paragraph(valor, fonteTextoNegrito());

        celula.addElement(tituloParagrafo);
        celula.addElement(valorParagrafo);

        tabela.addCell(celula);
    }

    private void adicionarCabecalhoTabela(PdfPTable tabela, String titulo) {
        PdfPCell celula = new PdfPCell(new Phrase(titulo, fonteTextoBrancaNegrito()));
        celula.setBackgroundColor(AZUL_ESCURO);
        celula.setPadding(7);
        celula.setBorderColor(AZUL_ESCURO);
        tabela.addCell(celula);
    }

    private void adicionarCelulaTabela(PdfPTable tabela, String valor) {
        PdfPCell celula = new PdfPCell(new Phrase(valor, fonteTexto()));
        celula.setPadding(7);
        celula.setBorderColor(CINZA_BORDA);
        tabela.addCell(celula);
    }

    private BigDecimal calcularValorTotal(List<OrdemServico> ordens) {
        return ordens.stream()
                .map(OrdemServico::getValorTotal)
                .filter(valor -> valor != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String obterPrimeiroServicoDaOrdem(Long idOs) {
        if (idOs == null) {
            return "NÃO INFORMADO";
        }

        ItemOrdemServico item = itemOrdemServicoRepository
                .findFirstByOrdemServico_IdOs(idOs)
                .orElse(null);

        if (item == null) {
            return "NÃO INFORMADO";
        }

        return obterNomeServico(item);
    }

    private String obterNomeCliente(OrdemServico ordem) {
        if (ordem.getCliente() == null || ordem.getCliente().getNome() == null) {
            return "NÃO INFORMADO";
        }

        return ordem.getCliente().getNome();
    }

    private String obterEnderecoCliente(OrdemServico ordem) {
        if (ordem.getCliente() == null) {
            return "NÃO INFORMADO";
        }

        StringBuilder endereco = new StringBuilder();

        if (ordem.getCliente().getEndereco() != null && !ordem.getCliente().getEndereco().isBlank()) {
            endereco.append(ordem.getCliente().getEndereco());
        }

        if (ordem.getCliente().getNumero() != null && !ordem.getCliente().getNumero().isBlank()) {
            endereco.append(", Nº ").append(ordem.getCliente().getNumero());
        }

        if (ordem.getCliente().getBairro() != null && !ordem.getCliente().getBairro().isBlank()) {
            endereco.append(" - ").append(ordem.getCliente().getBairro());
        }

        if (ordem.getCliente().getCidade() != null) {
            endereco.append(" - ")
                    .append(ordem.getCliente().getCidade().getNome())
                    .append("/")
                    .append(ordem.getCliente().getCidade().getUf());
        }

        return endereco.length() > 0 ? endereco.toString() : "NÃO INFORMADO";
    }

    private String obterNomeServico(ItemOrdemServico item) {
        if (item.getServico() == null || item.getServico().getNome() == null) {
            return "NÃO INFORMADO";
        }

        return item.getServico().getNome();
    }

    private String formatarData(LocalDateTime data) {
        if (data == null) {
            return "NÃO INFORMADA";
        }

        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private String formatarMoeda(BigDecimal valor) {
        if (valor == null) {
            return "R$ 0,00";
        }

        return NumberFormat
                .getCurrencyInstance(new Locale("pt", "BR"))
                .format(valor);
    }

    private String valorTexto(Object valor) {
        return valor != null && !valor.toString().isBlank()
                ? valor.toString()
                : "NÃO INFORMADO";
    }

    private Font fonteTituloBranca() {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, Color.WHITE);
    }

    private Font fonteSubtituloBranca() {
        return FontFactory.getFont(FontFactory.HELVETICA, 9, Color.WHITE);
    }

    private Font fonteSecao() {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, AZUL_ESCURO);
    }

    private Font fonteSecaoBranca() {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
    }

    private Font fonteDestaque() {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, AZUL_ESCURO);
    }

    private Font fonteTexto() {
        return FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
    }

    private Font fonteTextoNegrito() {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
    }

    private Font fonteTextoPequena() {
        return FontFactory.getFont(FontFactory.HELVETICA, 8, Color.DARK_GRAY);
    }

    private Font fonteTextoPequenaNegrito() {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.DARK_GRAY);
    }

    private Font fonteTextoBrancaNegrito() {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.WHITE);
    }
}