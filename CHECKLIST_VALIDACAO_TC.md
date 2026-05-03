# Checklist manual de validação final (TC) - OrdemPro

## 1) Autenticação e acesso
- [ ] Acessar `/login` e validar carregamento da tela.
- [ ] Tentar acessar `/ordens` sem login e confirmar redirecionamento para autenticação.
- [ ] Efetuar login com usuário válido e validar acesso às telas internas.
- [ ] Tentar acessar rota restrita com perfil sem permissão e validar tela de acesso negado.

## 2) Fluxo de clientes
- [ ] Cadastrar cliente com dados válidos (nome, CPF, telefone e cidade).
- [ ] Editar cliente existente e confirmar persistência das alterações.
- [ ] Pesquisar cliente por nome e validar filtro.
- [ ] Validar formatação exibida de CPF e telefone na listagem.

## 3) Fluxo de cidades
- [ ] Listar cidades e confirmar carregamento sem erros.
- [ ] Cadastrar nova cidade (quando permitido no ambiente).
- [ ] Validar busca por nome/UF da cidade.

## 4) Fluxo de ordens de serviço
- [ ] Criar ordem com cliente, descrição e valor.
- [ ] Editar ordem existente e confirmar atualização do item/valor.
- [ ] Validar listagem padrão exibindo as últimas 5 ordens sem filtro.
- [ ] Aplicar filtros por status, cliente e serviço e validar resultados.
- [ ] Cancelar ordem em status ABERTA e confirmar transição para CANCELADA.
- [ ] Tentar cancelar ordem FINALIZADA e validar bloqueio com mensagem adequada.

## 5) Recursos complementares
- [ ] Gerar PDF da ordem e validar download do arquivo.
- [ ] Enviar e-mail da ordem (com SMTP configurado) e validar feedback de sucesso.

## 6) Segurança e robustez
- [ ] Repetir ações de salvar/editar com campos obrigatórios vazios e validar mensagens de erro.
- [ ] Informar valor inválido no formulário de ordem e validar tratamento.
- [ ] Validar ausência de erro 500 em fluxos comuns (listar, salvar, cancelar, gerar PDF).
