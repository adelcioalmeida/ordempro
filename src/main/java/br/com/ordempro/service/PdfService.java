package br.com.ordempro.service;

import br.com.ordempro.model.ItemOrdemServico;
import br.com.ordempro.model.OrdemServico;
import br.com.ordempro.repository.ItemOrdemServicoRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PdfService {

    private final ItemOrdemServicoRepository itemOrdemServicoRepository;

    public PdfService(ItemOrdemServicoRepository itemOrdemServicoRepository) {
        this.itemOrdemServicoRepository = itemOrdemServicoRepository;
    }

    public byte[] gerarPdfOrdemServico(OrdemServico ordemServico) throws Exception {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("ORDEM DE SERVIÇO"));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("ID da Ordem: " + valorTexto(ordemServico.getIdOs())));
            document.add(new Paragraph("Cliente: " + obterNomeCliente(ordemServico)));
            document.add(new Paragraph("Status: " + valorTexto(ordemServico.getStatus())));
            document.add(new Paragraph("Data de Abertura: " + formatarData(ordemServico.getDataAbertura(), formatter)));
            document.add(new Paragraph("Data Prevista de Conclusão: " + formatarData(ordemServico.getDataPrevistaConclusao(), formatter)));
            document.add(new Paragraph("Observação: " + valorTexto(ordemServico.getObservacao())));
            document.add(new Paragraph("Valor Total: " + valorTexto(ordemServico.getValorTotal())));
            document.add(new Paragraph(" "));

            List<ItemOrdemServico> itens = itemOrdemServicoRepository.findByOrdemServico_IdOs(ordemServico.getIdOs());

            document.add(new Paragraph("ITENS DA ORDEM"));
            document.add(new Paragraph(" "));

            if (itens.isEmpty()) {
                document.add(new Paragraph("Nenhum item cadastrado para esta ordem."));
            } else {
                for (ItemOrdemServico item : itens) {
                    document.add(new Paragraph("Serviço: " + obterNomeServico(item)));
                    document.add(new Paragraph("Descrição: " + valorTexto(item.getDescricao())));
                    document.add(new Paragraph("Valor: " + valorTexto(item.getValor())));
                    document.add(new Paragraph(" "));
                }
            }

            document.close();
            return out.toByteArray();

        } catch (DocumentException e) {
            throw new Exception("Erro ao gerar PDF da ordem de serviço.", e);
        }
    }

    private String obterNomeCliente(OrdemServico ordemServico) {
        if (ordemServico.getCliente() == null || ordemServico.getCliente().getNome() == null) {
            return "NÃO INFORMADO";
        }
        return ordemServico.getCliente().getNome();
    }

    private String obterNomeServico(ItemOrdemServico item) {
        if (item.getServico() == null || item.getServico().getNome() == null) {
            return "NÃO INFORMADO";
        }
        return item.getServico().getNome();
    }

    private String formatarData(java.time.LocalDateTime data, DateTimeFormatter formatter) {
        if (data == null) {
            return "NÃO INFORMADA";
        }
        return data.format(formatter);
    }

    private String valorTexto(Object valor) {
        return valor != null ? valor.toString() : "NÃO INFORMADO";
    }
}