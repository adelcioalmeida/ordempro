# OrdemPro

Sistema web interno de gestão de ordens de serviço, desenvolvido como Trabalho de Curso em Análise e Desenvolvimento de Sistemas.

## Sobre o projeto

O OrdemPro foi criado para auxiliar pequenas empresas de manutenção predial no controle de clientes, cidades, serviços e ordens de serviço.

A aplicação substitui controles manuais, como anotações em papel e planilhas, por um fluxo centralizado com autenticação, controle de acesso por perfil, cadastro de clientes, gerenciamento de ordens de serviço, geração de PDF e envio de informações por e-mail.

## Tecnologias

- Java 17+
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL
- Thymeleaf
- HTML, CSS e JavaScript
- Maven

## Principais funcionalidades

- Autenticação de usuários
- Controle de acesso por perfis ADMIN, GERENTE e VENDEDOR
- Cadastro, edição e consulta de clientes
- Cadastro e consulta de cidades
- Cadastro e gerenciamento de serviços
- Criação, edição, consulta e cancelamento de ordens de serviço conforme permissões
- Controle de status das ordens
- Geração de PDF
- Envio de informações por e-mail
- Interface responsiva

## Arquitetura

O projeto utiliza uma organização em camadas para separar responsabilidades:

```text
br.com.ordempro
├── config
├── controller
├── dto
├── model
├── repository
├── service
└── OrdemproApplication.java
```

### config
Contém configurações gerais da aplicação, incluindo autenticação e autorização com Spring Security.

### controller
Recebe as requisições da aplicação web e encaminha as operações para a camada de serviço.

### dto
Contém objetos usados para transportar dados entre formulários, controllers e regras de negócio.

### model
Contém as entidades JPA que representam as principais tabelas do banco de dados.

### repository
Responsável pelo acesso aos dados utilizando Spring Data JPA.

### service
Concentra regras de negócio e operações utilizadas pelos controllers.

## Banco de dados

O sistema utiliza MySQL. Entre as principais entidades estão:

- usuários
- funções/perfis
- clientes
- cidades
- serviços
- ordens de serviço
- itens da ordem de serviço

## Segurança

A autenticação e o controle de acesso são realizados com Spring Security.

As permissões são separadas por perfil:

| Perfil | Acesso principal |
| --- | --- |
| ADMIN | Acesso administrativo completo |
| GERENTE | Acesso aos módulos operacionais e funções de gestão |
| VENDEDOR | Acesso aos cadastros e ordens dentro das permissões definidas |

Rotas internas exigem autenticação e o logout invalida a sessão do usuário.

## Configuração de e-mail

As credenciais de SMTP não ficam armazenadas no repositório. Para utilizar o envio de e-mail, configure as variáveis de ambiente:

```text
MAIL_USERNAME
MAIL_PASSWORD
```

## Como executar

### Pré-requisitos

- Java 17 ou superior
- MySQL
- Git

O projeto inclui Maven Wrapper, portanto não é obrigatório instalar o Maven separadamente.

### Passos

1. Clone o repositório.
2. Crie o banco `ordempro` no MySQL.
3. Configure usuário e senha do banco em `src/main/resources/application.properties` ou adapte para variáveis de ambiente.
4. Caso utilize envio de e-mail, defina `MAIL_USERNAME` e `MAIL_PASSWORD` no ambiente.
5. Execute a aplicação pelo Maven Wrapper ou pela classe `OrdemproApplication.java`.
6. Acesse:

```text
http://localhost:8081/login
```

## Testes e validação

Durante o desenvolvimento foram realizados testes manuais dos principais fluxos, incluindo autenticação, controle de acesso, clientes, cidades, ordens de serviço, geração de PDF, envio de e-mail e responsividade.

## Status

Projeto concluído como Trabalho de Curso, com as principais funcionalidades implementadas e validadas.

## Autores

Adelcio Junior Lima de Almeida  
Leonardo Satim

Curso: Análise e Desenvolvimento de Sistemas
