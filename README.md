# ♿ Projeto Mapa Interativo de Acessibilidade 🗺️

O **Mapa Interativo de Acessibilidade** é um sistema colaborativo desenvolvido para mapear, coletar e visualizar dados sobre a acessibilidade de locais públicos e privados. O objetivo principal é promover a inclusão social, fornecer informações vitais à comunidade e incentivar a melhoria da infraestrutura urbana.

---

## 🚀 Tecnologias Utilizadas

O projeto utiliza uma arquitetura *full-stack* moderna, com Java no *Back-end* para a API de dados e tecnologias web padrão para a interface.

### Back-end (API de Dados)
* **Linguagem:** **Java**
* **Framework:** **Spring Boot** (Facilita a criação de APIs REST robustas).
* **Gerenciador de Dependências:** **Maven** (Usado para gerenciar as bibliotecas e o *build* do projeto).
* **Banco de Dados:** **MySQL** (Relacional, usado para armazenar usuários, avaliações e dados dos pontos).
* **ORM (Mapeamento Objeto-Relacional):** **JPA/Hibernate**.
* **Serviço:** Escutando na porta padrão **`8080`**.

### Front-end (Interface do Mapa)
* **Linguagens:** HTML5, CSS3, **JavaScript** (responsável pela lógica de formulários e comunicação com a API).
* **Visualização:** [Inclua a biblioteca de mapa utilizada, ex: Leaflet.js, Google Maps API, etc.].
* **Servidor de Desenvolvimento:** Live Server (rodando na porta **`5500`**).

---

## ⚙️ Instalação e Configuração

Para executar o projeto, siga os passos abaixo para configurar o ambiente de desenvolvimento e as conexões de banco de dados.

### 1. Pré-requisitos

Certifique-se de que os seguintes softwares estão instalados e configurados:
* **Java Development Kit (JDK) 17+** (ou a versão compatível com seu projeto Spring Boot).
* **Apache Maven**.
* **Git**.
* **MySQL Server** (rodando localmente, geralmente na porta `3306`).

### 2. Clonagem do Repositório

```bash
git clone https://github.com/HevellynMont/mapa-de-acessibilidade.git
```

### 3. Instalação de Dependências

O Maven cuidará da instalação de todas as dependências, incluindo o Spring Boot e o MySQL Connector/J.

Executa a limpeza e instala as dependências
```bash
mvn clean install
```

## ▶️ Como Rodar o Projeto

O projeto exige que tanto o Back-end (API) quanto o Front-end (Interface) estejam ativos.

### 1. Iniciar o Back-end (Spring Boot)

Garanta que o MySQL Server está rodando antes de iniciar o Spring Boot.

  Via IDE (Recomendado): Execute a aplicação pr
    incipal do Spring Boot a partir da sua IDE (IntelliJ, Eclipse, etc.).

```bash
mvn spring-boot:run
```

A API estará acessível em http://localhost:8080

### 2. Iniciar o Front-end (Live Server)

O Front-end serve os arquivos HTML/JS e fará as requisições para a porta 8080.

  Navegue até a pasta que contém seus arquivos HTML (ex: src/main/resources/static-FINAL/html/).

  Abra o arquivo principal (login.html) usando o Live Server. O Front-end estará acessível em http://127.0.0.1:5500.
