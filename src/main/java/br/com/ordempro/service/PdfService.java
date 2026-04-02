package br.com.ordempro.service;

import br.com.ordempro.model.ItemOrdemServico;
import br.com.ordempro.model.OrdemServico;
import br.com.ordempro.repository.ItemOrdemServicoRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {

    private final ItemOrdemServicoRepository itemOrdemServicoRepository;

    public PdfService(ItemOrdemServicoRepository itemOrdemServicoRepository) {
        this.itemOrdemServicoRepository = itemOrdemServicoRepository;
    }

    public byte[] gerarPdfOrdemServico(OrdemServico ordemServico) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document();
        PdfWriter.getInstance(document, out);

        document.open();

        document.add(new Paragraph("ORDEM DE SERVIÇO"));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("ID da Ordem: " + ordemServico.getIdOs()));
        document.add(new Paragraph("Cliente: " + ordemServico.getCliente().getNome()));
        document.add(new Paragraph("Status: " + ordemServico.getStatus()));
        document.add(new Paragraph("Data de Abertura: " + ordemServico.getDataAbertura()));
        document.add(new Paragraph("Data Prevista de Conclusão: " + ordemServico.getDataPrevistaConclusao()));
        document.add(new Paragraph("Observação: " + ordemServico.getObservacao()));
        document.add(new Paragraph("Valor Total: " + ordemServico.getValorTotal()));
        document.add(new Paragraph(" "));

        List<ItemOrdemServico> itens = itemOrdemServicoRepository.findByOrdemServico_IdOs(ordemServico.getIdOs());

        document.add(new Paragraph("ITENS DA ORDEM"));
        document.add(new Paragraph(" "));

        for (ItemOrdemServico item : itens) {
            document.add(new Paragraph("Serviço: " + item.getServico().getNome()));
            document.add(new Paragraph("Descrição: " + item.getDescricao()));
            document.add(new Paragraph("Valor: " + item.getValor()));
            document.add(new Paragraph(" "));
        }

        document.close();

        return out.toByteArray();
    }
}