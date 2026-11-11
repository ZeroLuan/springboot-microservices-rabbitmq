# 📧 Email Microservice (`ms-email`)

Microsserviço responsável pelo consumo assíncrono de mensagens da fila do **RabbitMQ** e disparo de e-mails via **SMTP (JavaMailSender)**, atuando como **Consumer (Consumidor)** do ecossistema.

---

## 🎯 Contexto e Objetivo do Projeto

Este projeto foi desenvolvido como parte prática de estudos sobre **Arquitetura de Microsserviços**, **Mensageria Assíncrona** e uso de **Message Brokers (RabbitMQ)**.

O objetivo principal é demonstrar o desacoplamento entre serviços através de comunicação assíncrona orientada a eventos:

1. **`ms-user` (Producer):** Quando um novo usuário se cadastra, o serviço de usuários persiste o registro em sua base (`ms-user`) e publica um evento JSON na fila `default.email` do RabbitMQ.
2. **`ms-email` (Este serviço - Consumer):** Escuta continuamente a fila `default.email` através do `@RabbitListener`. Assim que uma mensagem chega ao broker:
   * Se o `ms-email` estiver ativo, ele consome a mensagem imediatamente, envia o e-mail pelo servidor SMTP e grava o registro na tabela `TB_EMAILS` com o status `SENT` (ou `ERROR` em caso de falha no SMTP), confirmando a leitura (*Ack*) para o RabbitMQ remover a mensagem da fila.
   * Caso o `ms-email` esteja fora do ar para manutenção, as mensagens permanecem retidas com segurança na fila do RabbitMQ (`Ready`) e são processadas automaticamente assim que o serviço volta a subir.

```mermaid
flowchart LR
    Broker[["RabbitMQ Broker (Fila: default.email)"]] -->|@RabbitListener| EmailMS["ms-email (Consumer :8082)"]
    EmailMS -->|JavaMailSender (SMTP :587)| Gmail["Servidor SMTP (Gmail)"]
    EmailMS -->|Persiste Status (SENT / ERROR)| EmailDB[("PostgreSQL (ms-email)")]
    Client["Cliente HTTP"] -->|GET /emails| EmailMS
```

---

## 🛠 Principais Tecnologias

* **Linguagem:** Java 21
* **Framework:** Spring Boot 4.1.2 (Spring Web MVC, Spring Data JPA, Spring Mail, Spring Validation)
* **Mensageria:** Spring AMQP + RabbitMQ (CloudAMQP)
* **Envio de E-mail:** Spring Boot Starter Mail (`JavaMailSender` + SMTP Gmail)
* **Banco de Dados:** PostgreSQL (`ms-email`)
* **Produtividade e Mapeamento:** Lombok + MapStruct
* **Tratamento de Erros:** `@RestControllerAdvice` padronizado (`ApiExceptionHandler`)

---

## 📂 Estrutura de Pacotes

```text
com.ms.email
├── config/             # Declaração da Queue durável no RabbitMQ e conversor JSON
├── consumer/           # Listener da fila RabbitMQ (@RabbitListener)
├── controller/         # Endpoints REST para consulta de e-mails enviados (/emails)
├── dto/                # Records de mensagem da fila e resposta da API
├── entity/             # Entidade JPA (TB_EMAILS)
├── enums/              # Enum StatusEmail (SENT, ERROR)
├── exception/          # Exceções de negócio e Handler global (@RestControllerAdvice)
├── mapper/             # Mapeamento DTO <-> Entity via MapStruct
├── repository/         # Persistência de dados com Spring Data JPA
└── service/            # Envio de e-mail via SMTP e persistência do histórico
```

---

## ⚙️ Configuração e Variáveis de Ambiente

Todas as credenciais de banco, URL do broker RabbitMQ e Senha de App do Gmail foram isoladas em variáveis de ambiente. O serviço importa automaticamente um arquivo `.env` local (ignorado pelo Git).

1. Copie o arquivo de exemplo na raiz do serviço:
   ```bash
   cp .env.example .env
   ```
2. Preencha as variáveis no arquivo `.env`:

| Variável | Descrição | Valor Padrão |
| :--- | :--- | :--- |
| `SERVER_PORT` | Porta HTTP do microsserviço | `8082` |
| `DB_URL` | URL JDBC do banco PostgreSQL | `jdbc:postgresql://localhost:5432/ms-email` |
| `DB_USERNAME` | Usuário do banco de dados | `postgres` |
| `DB_PASSWORD` | Senha do banco de dados | *(Obrigatório)* |
| `RABBITMQ_ADDRESSES` | URL AMQPS do broker RabbitMQ / CloudAMQP | *(Obrigatório)* |
| `BROKER_QUEUE_EMAIL_NAME` | Nome da fila escutada no broker | `default.email` |
| `MAIL_HOST` | Host do servidor SMTP | `smtp.gmail.com` |
| `MAIL_PORT` | Porta do servidor SMTP (STARTTLS) | `587` |
| `MAIL_USERNAME` | E-mail remetente (conta Gmail) | *(Obrigatório)* |
| `MAIL_PASSWORD` | Senha de App gerada na conta Google | *(Obrigatório)* |

---

## ⚡ Como Executar

1. Certifique-se de que o banco de dados **`ms-email`** exista no seu PostgreSQL local.
2. Configure o arquivo `.env` com suas credenciais do CloudAMQP e Senha de App do Gmail.
3. Execute a aplicação via Maven ou diretamente pela sua IDE:
   ```bash
   ./mvnw spring-boot:run
   ```

---

## 📌 Endpoints Disponíveis (Auditoria / Histórico)

Além de atuar como consumidor da fila do RabbitMQ em background, o serviço disponibiliza endpoints REST para consulta do histórico de envios:

### 1. Listar Histórico de E-mails
* **GET** `/emails`
* **Response:** `200 OK` com a lista de todos os e-mails processados e seus respectivos status (`SENT` ou `ERROR`).

### 2. Buscar E-mail por ID
* **GET** `/emails/{emailId}`
* **Response:** `200 OK` ou `404 Not Found`.

---

**Autor:** Luan Alves de Souza

