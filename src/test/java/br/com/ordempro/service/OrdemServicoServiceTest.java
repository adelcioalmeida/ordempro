package br.com.ordempro.service;

import br.com.ordempro.model.ItemOrdemServico;
import br.com.ordempro.model.OrdemServico;
import br.com.ordempro.model.Servico;
import br.com.ordempro.repository.ItemOrdemServicoRepository;
import br.com.ordempro.repository.OrdemServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdemServicoServiceTest {
    @Mock private OrdemServicoRepository ordemServicoRepository;
    @Mock private ItemOrdemServicoRepository itemRepository;
    @InjectMocks private OrdemServicoService ordemServicoService;
    @Captor private ArgumentCaptor<OrdemServico> ordemServicoCaptor;
    private OrdemServico ordemServico;

    @BeforeEach
    void setUp() {
        ordemServico = new OrdemServico();
        ordemServico.setIdOs(10L);
        ordemServico.setStatus("ABERTA");
    }

    @Test
    void deveCancelarOrdemQuandoEncontrada() {
        when(ordemServicoRepository.findById(10L)).thenReturn(Optional.of(ordemServico));
        ordemServicoService.cancelarPorId(10L);
        verify(ordemServicoRepository).save(ordemServicoCaptor.capture());
        assertEquals("CANCELADA", ordemServicoCaptor.getValue().getStatus());
    }

    @Test
    void naoDeveSalvarQuandoOrdemNaoExiste() {
        when(ordemServicoRepository.findById(99L)).thenReturn(Optional.empty());
        ordemServicoService.cancelarPorId(99L);
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    void devePriorizarDescricaoDoItemAoBuscarDescricaoServico() {
        ItemOrdemServico item = new ItemOrdemServico();
        item.setDescricao("Troca de disjuntor");
        when(itemRepository.findFirstByOrdemServico_IdOs(1L)).thenReturn(Optional.of(item));
        assertEquals("Troca de disjuntor", ordemServicoService.buscarDescricaoServico(1L));
    }

    @Test
    void deveUsarNomeServicoQuandoDescricaoForVazia() {
        ItemOrdemServico item = new ItemOrdemServico();
        item.setDescricao("  ");
        Servico servico = new Servico();
        servico.setNome("Instalação elétrica");
        item.setServico(servico);
        when(itemRepository.findFirstByOrdemServico_IdOs(2L)).thenReturn(Optional.of(item));
        assertEquals("Instalação elétrica", ordemServicoService.buscarDescricaoServico(2L));
    }

    @Test
    void deveRetornarPadraoQuandoNaoHaItem() {
        when(itemRepository.findFirstByOrdemServico_IdOs(3L)).thenReturn(Optional.empty());
        assertEquals("NÃO INFORMADO", ordemServicoService.buscarDescricaoServico(3L));
    }

    @Test
    void deveBuscarPorIdOuRetornarNulo() {
        when(ordemServicoRepository.findById(10L)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoRepository.findById(11L)).thenReturn(Optional.empty());
        assertEquals(ordemServico, ordemServicoService.buscarPorId(10L));
        assertNull(ordemServicoService.buscarPorId(11L));
    }
}
