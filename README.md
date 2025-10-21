# 🛒 Projeto — Gerenciador de Produtos (Spring Boot + H2 + Frontend Simples)

> Este é um projeto de exemplo que demonstra como criar um **Gerenciador de Produtos** usando tecnologias modernas. Ele inclui um **backend** (a parte que lida com a lógica e os dados) feito com **Spring Boot** (Java), um **banco de dados H2** (que funciona na memória do seu computador) e um **frontend** (a interface que você vê no navegador) simples, feito com HTML, CSS e JavaScript.
> 
> Com ele, você pode **adicionar, listar e remover produtos** tanto pela interface do navegador quanto por meio de requisições de API (se você for mais avançado e quiser usar ferramentas como Postman ou cURL).

---

## 🗂️ Índice
1. [Visão geral](#-visão-geral)
2. [Pré-requisitos](#-pré-requisitos)
3. [Estrutura do projeto](#-estrutura-do-projeto)
4. [Configuração do banco H2](#-configuração-do-banco-h2)
5. [Como clonar e rodar o projeto](#️-como-clonar-e-rodar-o-projeto)
6. [Testes via Postman / cURL](#-testes-via-postman--curl)
7. [Verificar frontend](#-verificar-frontend)
8. [Verificar H2 Console](#-verificar-h2-console)
9. [Erros comuns e soluções](#-erros-comuns-e-soluções)
10. [Notas finais](#-notas-finais)
11. [Autores e créditos](#-autores-e-créditos)

---

## 💡 Visão geral

O projeto demonstra um **CRUD básico de produtos**, com as seguintes tecnologias:

- **Spring Boot (Java 17+)**
- **Banco H2 em memória**
- **Frontend estático simples** (HTML, CSS e JS)
- **Integração completa via API REST**
- **Sem Spring Security** (liberado para testes)

O objetivo principal é demonstrar de forma clara e visual como o backend e o frontend se comunicam, sendo perfeito para quem está aprendendo ou para apresentações.

---

## 🧰 Pré-requisitos

Para rodar este projeto, você precisará ter instalado:

-   **Java Development Kit (JDK) 17** ou versão superior. Você pode baixá-lo do site oficial da Oracle ou usar um gerenciador de pacotes como o SDKMAN!.
-   **Visual Studio Code (VS Code)**: Um editor de código leve e poderoso. Você pode baixá-lo em [code.visualstudio.com](https://code.visualstudio.com/).
-   **Extensões do VS Code para Java e Spring Boot**: Dentro do VS Code, vá para a aba de Extensões (Ctrl+Shift+X ou Cmd+Shift+X) e procure por:
    -   `Extension Pack for Java`: Este pacote inclui tudo o que você precisa para desenvolver em Java no VS Code.
    -   `Spring Boot Extension Pack`: Essencial para trabalhar com projetos Spring Boot, oferecendo funcionalidades como execução e depuração.
-   **Navegador Web**: Qualquer navegador moderno (Chrome, Edge, Firefox) para acessar a interface do projeto e o console do H2.
-   **Git**: Para clonar o projeto do GitHub. Se você usa o VS Code, ele já vem com integração Git.
-   **Postman** (opcional): Uma ferramenta para testar APIs REST, caso você queira interagir com o backend diretamente.

---

## 🏗️ Estrutura do projeto

```
src/
├── main/
│   ├── java/
│   │   └── com/br/mapa_de_acessibilidade/mapa_de_acessibilidade/
│   │       ├── controller/
│   │       │   └── ProdutoController.java
│   │       ├── model/
│   │       │   └── Produto.java
│   │       ├── repository/
│   │       │   └── ProdutoRepository.java
│   │       └── service/
│   │           └── ProdutoService.java
│   └── resources/
│       ├── static/
│       │   ├── html/
│       │   │   └── produtos.html
│       │   ├── css/
│       │   │   └── produtos.css
│       │   └── js/
│       │       └── produtos.js
│       └── application.properties
```

---

## ⚙️ Configuração do banco H2

Arquivo: `src/main/resources/application.properties`

```properties
server.port=8080

# Banco de dados em memória
spring.datasource.url=jdbc:h2:mem:meubanco
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# H2 Console (web)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

## ▶️ Como clonar e rodar o projeto

Siga estes passos para colocar o projeto para funcionar:

### 🌀 1. Clonar o repositório

1.  **Abra o Terminal ou Git Bash** (se você usa Windows) e execute o comando abaixo para baixar o projeto:
    ```bash
    git clone https://github.com/SEU_USUARIO/gerenciador-produtos.git
    ```
    *Substitua `SEU_USUARIO` pelo nome de usuário do GitHub onde o projeto está hospedado.*

2.  **Entre na pasta do projeto**:
    ```bash
    cd gerenciador-produtos
    ```

3.  **Abra o projeto no VS Code**: No terminal, digite:
    ```bash
    code .
    ```
    Ou, abra o VS Code, vá em `Arquivo > Abrir Pasta...` e selecione a pasta `gerenciador-produtos`.

### ⚙️ 2. Rodar o projeto com a extensão Spring Boot

1.  No VS Code, abra a **paleta de comandos** (Ctrl+Shift+P ou Cmd+Shift+P).
2.  Digite `Spring Boot` e selecione a opção `Spring Boot: Run current project`.
3.  Aguarde alguns segundos. O VS Code vai compilar e iniciar o projeto. Você verá mensagens no terminal integrado do VS Code indicando que a aplicação está rodando.

### ✅ 3. Verifique se a aplicação está funcionando

Após iniciar o projeto, a aplicação estará disponível no seu navegador em:

👉 `http://localhost:8080`

Você pode abrir essa URL no seu navegador para ver o frontend do projeto.

## 🧪 Testes da API (com Postman ou cURL - Opcional)

Se você quiser testar a API do backend diretamente, sem usar o frontend, pode usar ferramentas como o Postman ou o cURL.

A **URL base** para as requisições é: `http://localhost:8080/produtos`

### ➕ Criar um Produto

Para adicionar um novo produto (requisição `POST`):

-   **No Postman**: Crie uma requisição `POST` para `http://localhost:8080/produtos`. No corpo da requisição (`Body`), selecione `raw` e `JSON`, e insira:
    ```json
    {
      "nome": "Cadeira Adaptada",
      "preco": 120.50
    }
    ```
-   **Com cURL (terminal)**:
    ```bash
    curl -X POST http://localhost:8080/produtos \
      -H "Content-Type: application/json" \
      -d '{"nome":"Cadeira Adaptada","preco":120.50}'
    ```

### 📋 Listar Produtos

Para ver todos os produtos (requisição `GET`):

-   **No Postman**: Crie uma requisição `GET` para `http://localhost:8080/produtos`.
-   **Com cURL (terminal)**:
    ```bash
    curl http://localhost:8080/produtos
    ```

### ♻️ Atualizar um Produto

Para mudar os dados de um produto existente (requisição `PUT`), por exemplo, o produto com ID `1`:

-   **No Postman**: Crie uma requisição `PUT` para `http://localhost:8080/produtos/1`. No corpo da requisição (`Body`), selecione `raw` e `JSON`, e insira:
    ```json
    {
      "nome": "Cadeira Nova",
      "preco": 150.0
    }
    ```
-   **Com cURL (terminal)**:
    ```bash
    curl -X PUT http://localhost:8080/produtos/1 \
      -H "Content-Type: application/json" \
      -d '{"nome":"Cadeira Nova","preco":150.0}'
    ```

### ❌ Deletar um Produto

Para remover um produto (requisição `DELETE`), por exemplo, o produto com ID `1`:

-   **No Postman**: Crie uma requisição `DELETE` para `http://localhost:8080/produtos/1`.
-   **Com cURL (terminal)**:
    ```bash
    curl -X DELETE http://localhost:8080/produtos/1
    ```

## 💻 Como usar o Frontend (Interface Web)

Depois que a aplicação Spring Boot estiver rodando, abra seu navegador e acesse a seguinte URL:

`http://localhost:8080/html/produtos.html`

Nessa página, você poderá:

*   **Adicionar novos produtos** (usando o formulário).
*   **Visualizar a lista de produtos** já cadastrados.
*   **Remover produtos** da lista.

A tabela de produtos será atualizada automaticamente após cada ação que você realizar.

## 🗄️ Verificar H2 Console (Banco de Dados)

Para verificar os dados diretamente no banco de dados H2 (que roda em memória):

1.  Certifique-se de que a aplicação Spring Boot esteja rodando (conforme as instruções acima).
2.  Abra seu navegador e acesse: `http://localhost:8080/h2-console`
3.  Na tela de login do H2 Console, preencha os campos com as seguintes informações:
    -   **Driver Class**: `org.h2.Driver`
    -   **JDBC URL**: `jdbc:h2:mem:meubanco`
    -   **User Name**: `sa`
    -   **Password**: (deixe em branco)
4.  Clique no botão "Connect".
5.  Na tela seguinte, você pode executar comandos SQL. Para ver os produtos cadastrados, digite:
    ```sql
    SELECT * FROM PRODUTO;
    ```
    E clique em "Run" para executar a consulta.

## 🚨 Erros Comuns e Como Resolver

Se você encontrar algum problema, consulte esta tabela para possíveis soluções:

| Erro                                        | O que pode estar acontecendo                               | Como resolver                                                                |
| :------------------------------------------ | :--------------------------------------------------------- | :--------------------------------------------------------------------------- |
| ❌ `No static resource html/produtos.html`  | O arquivo `produtos.html` não está no lugar certo.         | Verifique se o arquivo está em `src/main/resources/static/html/`.            |
| ⚠️ `Database "mem:meubanco" not found`      | A aplicação Spring Boot não foi iniciada.                  | Inicie a aplicação Spring Boot antes de tentar acessar o H2 Console.         |
| 🔒 `Tela de login (Spring Security)`        | Alguma configuração de segurança do Spring Boot está ativa. | Se você não adicionou segurança intencionalmente, remova a dependência `spring-boot-starter-security` do seu arquivo `pom.xml`. |
| 🔍 `404 em /produtos`                       | O controlador da API (`ProdutoController`) não está sendo encontrado. | Garanta que o pacote do seu controlador (`com.br.mapa_de_acessibilidade.mapa_de_acessibilidade.controller`) esteja dentro do pacote base da sua aplicação Spring Boot (onde está a classe principal com `@SpringBootApplication`). |

## 🧾 Notas Importantes

-   **Banco de Dados H2**: O banco de dados H2 usado neste projeto é **em memória**, o que significa que todos os dados são apagados cada vez que você reinicia a aplicação. Ele é perfeito para testes e aprendizado, mas não para projetos que precisam guardar informações de forma permanente.
-   **Uso em Produção**: Este projeto é ideal para **demonstrações e estudos**. Evite usar o H2 Console em ambientes de produção (projetos reais que estão no ar), pois ele não foi feito para isso.

## 📌 Autores e créditos

Projeto desenvolvido para fins educacionais por Hevellyn Monteiro Medeiros e equipe.
Tecnologias utilizadas: Spring Boot, H2 Database, HTML5, CSS3 e JavaScript (ES6).
