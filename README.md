# OrdemPro

Sistema interno de gestão de ordens de serviço desenvolvido como Trabalho de Curso do curso de Análise e Desenvolvimento de Sistemas.

## 1. Descrição do Projeto

O OrdemPro é um sistema web interno criado para auxiliar pequenas empresas de manutenção predial no controle de clientes, cidades, serviços e ordens de serviço.

O objetivo do sistema é substituir controles manuais, como anotações em papel ou planilhas, por uma aplicação organizada, permitindo o cadastro de clientes, abertura de ordens de serviço, controle de status, geração de PDF e envio de informações por e-mail.

## 2. Tecnologias Utilizadas

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL
- Thymeleaf
- HTML
- CSS
- JavaScript
- Maven

## 3. Funcionalidades do Sistema

### Login

O sistema possui autenticação de usuários. Apenas usuários cadastrados conseguem acessar as telas internas.

As rotas internas são protegidas pelo Spring Security, impedindo o acesso direto sem login.

### Clientes

Permite cadastrar, listar, editar e consultar clientes.

Cada cliente possui informações como nome, CPF, telefone e cidade.

### Cidades

O sistema utiliza uma tabela de cidades como base de apoio para o cadastro de clientes.

A exclusão de cidades não foi disponibilizada, pois a tabela contém cidades previamente cadastradas e pode estar vinculada a clientes.

### Ordens de Serviço

Permite cadastrar, listar, editar e acompanhar ordens de serviço.

A ordem de serviço possui cliente, serviço, status, valor total, data, observações e ações como geração de PDF e envio por e-mail.

### PDF

O sistema permite gerar PDF com os dados da ordem de serviço.

### E-mail

O sistema permite enviar informações da ordem de serviço por e-mail.

## 4. Estrutura do Projeto

O projeto segue uma organização em camadas, separando as responsabilidades do sistema para deixar o código mais limpo, organizado e fácil de manter.

A estrutura principal do código Java fica em:

br.com.ordempro
├── config
├── controller
├── dto
├── model
├── repository
├── service
└── OrdemproApplication.java

4.1 config

A pasta config contém as configurações gerais do sistema.

Nela ficam classes responsáveis por configurar segurança, autenticação, permissões de acesso e outros comportamentos globais da aplicação.

Exemplo:

SecurityConfig.java

O arquivo SecurityConfig.java configura o Spring Security, definindo quais páginas podem ser acessadas sem login e quais exigem autenticação. Também define regras de acesso por perfil de usuário, como ADMIN, GERENTE e VENDEDOR.

4.2 controller

A pasta controller contém as classes responsáveis por receber as requisições feitas pelo usuário no navegador.

Os controllers fazem a ligação entre as páginas HTML e as regras de negócio do sistema.

Exemplos:

LoginController.java
ClienteController.java
CidadeController.java
OrdemServicoController.java
UsuarioController.java

Essas classes recebem ações como cadastrar, editar, listar, salvar e excluir registros, chamando os services quando necessário.

4.3 dto

A pasta dto contém classes usadas para transportar dados entre a tela e o sistema.

DTO significa Data Transfer Object, ou seja, Objeto de Transferência de Dados.

Essas classes ajudam a receber os dados dos formulários sem expor diretamente as entidades do banco de dados.

Exemplo:

OrdemServicoFormDTO.java

Esse DTO pode armazenar dados como cliente, serviço, data, status, observação e valor da ordem de serviço.

4.4 model

A pasta model contém as entidades do sistema.

As entidades representam as tabelas do banco de dados. Cada classe dessa pasta normalmente possui anotações do JPA, como @Entity, @Table, @Id e @Column.

Exemplos:

Cliente.java
Cidade.java
Usuario.java
Funcao.java
Servico.java
OrdemServico.java
ItemOrdemServico.java

Essas classes definem os atributos principais do sistema e os relacionamentos entre as tabelas.

Por exemplo, uma ordem de serviço pode estar vinculada a um cliente, a um usuário responsável e a itens de serviço.

4.5 repository

A pasta repository contém as interfaces responsáveis pela comunicação com o banco de dados.

Essas interfaces utilizam o Spring Data JPA, permitindo realizar operações como salvar, buscar, listar e excluir dados sem precisar escrever todo o SQL manualmente.

Exemplos:

ClienteRepository.java
CidadeRepository.java
UsuarioRepository.java
ServicoRepository.java
OrdemServicoRepository.java

Esses repositories funcionam como a ponte entre o sistema Java e o banco de dados MySQL.

4.6 service

A pasta service contém as regras de negócio do sistema.

Ela evita que toda a lógica fique dentro dos controllers, deixando o projeto mais organizado.

Exemplos:

ClienteService.java
CidadeService.java
UsuarioService.java
ServicoService.java
OrdemServicoService.java

Os services são responsáveis por processar as informações antes de salvar, listar, editar ou consultar os dados.

4.7 Classe principal da aplicação

Na raiz do pacote principal fica a classe responsável por iniciar o sistema Spring Boot.

Exemplo:

OrdemproApplication.java

Essa classe possui o método main, que executa a aplicação e inicializa o servidor embutido do Spring Boot.

5. Estrutura das Páginas HTML

As páginas do sistema ficam na pasta:

src/main/resources/templates

Essa pasta contém os arquivos HTML utilizados pelo Thymeleaf.

Exemplos:

templates
├── login.html
├── clientes.html
├── cidades.html
├── ordens.html
├── formulario-cliente.html
├── formulario-cidade.html
├── formulario-ordem.html
└── acesso-negado.html
Função da pasta templates

A pasta templates guarda as telas do sistema que são exibidas no navegador.

O Thymeleaf permite que essas páginas recebam dados vindos do back-end, como lista de clientes, cidades, serviços e ordens de serviço.

6. Arquivos Estáticos

Os arquivos estáticos ficam na pasta:

src/main/resources/static

Essa pasta contém arquivos de estilo, imagens e scripts usados na interface do sistema.

Exemplo:

static
├── css
├── js
└── img
6.1 css

A pasta css contém os arquivos responsáveis pelo visual do sistema, como cores, espaçamentos, tabelas, botões, formulários e responsividade.

Exemplo:

sistema.css
login.css
6.2 js

A pasta js contém arquivos JavaScript usados para pequenas interações na interface, quando necessário.

6.3 img

A pasta img contém imagens utilizadas no sistema, como logotipo e ícones.

7. Banco de Dados

O banco de dados utilizado no projeto é o MySQL.

O sistema possui tabelas relacionadas entre si para armazenar usuários, clientes, cidades, serviços e ordens de serviço.

Principais tabelas:

usuarios
funcoes
clientes
cidades
servicos
ordens_servico
itens_ordem_servico
7.1 usuarios

Armazena os usuários que acessam o sistema.

Campos principais:

id do usuário
nome
e-mail
senha
status ativo
função/perfil de acesso
7.2 funcoes

Armazena os perfis de acesso do sistema.

Exemplos:

ADMIN
GERENTE
VENDEDOR

Esses perfis são utilizados pelo Spring Security para controlar o que cada usuário pode acessar.

7.3 cidades

Armazena as cidades utilizadas no cadastro de clientes.

A exclusão de cidades não foi disponibilizada no sistema, pois essa tabela funciona como base de apoio e pode estar vinculada aos clientes cadastrados.

7.4 clientes

Armazena os dados dos clientes atendidos pela empresa.

Campos principais:

nome
CPF
telefone
cidade
7.5 servicos

Armazena os serviços que podem ser utilizados nas ordens de serviço.

Exemplos:

manutenção
instalação
reparo
pintura
elétrica
hidráulica
7.6 ordens_servico

Armazena as ordens de serviço cadastradas no sistema.

Campos principais:

cliente
usuário responsável
status
data de abertura
data prevista de conclusão
observação
valor total
7.7 itens_ordem_servico

Armazena os itens vinculados a cada ordem de serviço.

Essa tabela permite que uma ordem de serviço tenha um ou mais serviços relacionados.

Campos principais:

ordem de serviço
serviço
descrição
valor
8. Segurança do Sistema

A segurança do sistema foi implementada com Spring Security.

O sistema possui autenticação por login e senha. As páginas internas só podem ser acessadas por usuários autenticados.

As permissões foram organizadas por perfil:

Perfil	Permissões principais
ADMIN	Acesso completo ao sistema, incluindo usuários
GERENTE	Acesso aos módulos principais e exclusão de ordens
VENDEDOR	Acesso aos cadastros e ordens de serviço, sem permissões administrativas

As rotas internas como /ordens, /clientes e /cidades exigem login. Caso o usuário tente acessar essas páginas diretamente sem autenticação, o sistema redireciona para a tela de login.

9. Funcionalidades do Sistema

O sistema possui as seguintes funcionalidades principais:

Funcionalidade	Descrição
Login	Permite acesso seguro ao sistema
Cadastro de clientes	Permite registrar clientes atendidos
Edição de clientes	Permite atualizar dados dos clientes
Consulta de cidades	Permite selecionar cidades no cadastro de clientes
Cadastro de ordens de serviço	Permite registrar uma nova ordem
Listagem de ordens de serviço	Exibe ordens cadastradas com cliente, serviço, status, valor e data
Edição de ordens de serviço	Permite atualizar informações da ordem
Geração de PDF	Permite gerar documento com os dados da ordem
Envio de e-mail	Permite enviar informações da ordem por e-mail
Controle de acesso	Restringe funcionalidades conforme o perfil do usuário
10. Testes Realizados

Foram realizados testes manuais para validar as principais funcionalidades do sistema.

Módulo	Teste realizado	Resultado
Login	Login com usuário e senha corretos	Aprovado
Login	Login com senha incorreta	Aprovado
Login	Logout do sistema	Aprovado
Segurança	Acesso direto a /ordens sem login	Aprovado após correção
Cidades	Listagem e utilização no cadastro	Aprovado
Clientes	Cadastro, edição e listagem	Aprovado
Ordens de Serviço	Cadastro, edição e listagem	Aprovado
PDF	Geração de PDF da ordem	Aprovado
E-mail	Envio de informações por e-mail	Aprovado
Responsividade	Teste em diferentes tamanhos de tela	Aprovado com ressalvas
11. Como Executar o Projeto
Pré-requisitos

Antes de executar o projeto, é necessário ter instalado:

Java 17 ou superior
MySQL
Maven
IntelliJ IDEA, Eclipse ou VS Code
Git
Passos para execução
Clonar o repositório:
git clone URL_DO_REPOSITORIO
Abrir o projeto na IDE.
Configurar o banco de dados no arquivo:
src/main/resources/application.properties
Criar o banco de dados no MySQL.
Conferir usuário e senha do banco no application.properties.
Executar a classe principal:
OrdemproApplication.java
Acessar o sistema no navegador:
http://localhost:8080/login
12. Status do Projeto

O sistema encontra-se em fase de finalização e testes para apresentação no Trabalho de Curso.

As principais funcionalidades já foram implementadas e validadas, restando apenas ajustes visuais e melhorias pontuais de responsividade.

13. Autores

Adelcio Junior Lima De Almeida
Leonardo Satim

Curso: Análise e Desenvolvimento de Sistemas

Projeto: OrdemPro

