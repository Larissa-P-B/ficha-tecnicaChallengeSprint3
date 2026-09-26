## Fabrini Soares - RM 557813 
## Guilherme Cezarino Simões - RM 557724 
## Larissa Pereira Biusse - RM 564068 
## Rodrigo Leme - RM 550266 
## Thamiris Almeida - RM 559155  
## Werbeth Nunes - RM 559067 


# 🚗 API SpeedSpec Ficha Técnica Automotiva

API REST desenvolvida com Spring Boot para gerenciamento de veículos e especificações técnicas automotivas.

---

# 📋 Funcionalidades

- Criar veículos
- Listar veículos
- Buscar veículo por ID
- Atualizar veículo completo (PUT)
- Atualizar parcialmente veículo (PATCH)
- Remover veículos
- Gerenciar especificações técnicas

---

# 🛠️ Tecnologias utilizadas

- Java 25
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Maven
- MySQL
- Swagger / OpenAPI

---

# 📦 Estrutura do Projeto

``` id="s5t6x7"
src/main/java/com/automotiva/ficha_tecnica
│
├── controller
│   └── Endpoints da API REST
│
├── entity
│   └── Entidades JPA
│
├── exception
│   └── Tratamento de exceções
│
├── repository
│   └── Interfaces JPA Repository
│
├── service
│   ├── dto
│   └── Regras de negócio
│
├── util
│   └── Classes utilitárias
│
└── FichaTecnicaApplication
    └── Classe principal da aplicação
```

| Método | Endpoint | Descrição |
|:---:|---|---|
| 🟧 PUT | `/api/veiculos/{id}` | Atualizar veículo |
| 🟥 DELETE | `/api/veiculos/{id}` | Deletar veículo |
| 🟩 PATCH | `/api/veiculos/{id}` | Atualizar parcialmente veículo |
| 🟦 GET | `/api/veiculos` | Listar veículos |
| 🟩 POST | `/api/veiculos` | Criar novo veículo |
| 🟩 POST | `/api/veiculos/especificacoes` | Buscar especificações do veículo |
| 🟩 POST | `/api/veiculos/comparar` | Comparar dois veículos |

# Criar banco MySQL
```
CREATE DATABASE automotiva_db;

```


# Exemplo busca especificações 

## POST /api/veiculos/especificacoes
```
{
  "marca": "ford",
  "modelo": "ranger",
  "versao": "XLT 3.0L V6 AT 26MY",
  "atributos": [
    "cilindrada" , "potencia"
  ]
}
```

# Exemplo busca comparação 

## POST /api/veiculos/comparar
```
{
  "veiculo1": {
    "marca": "FORD",
    "modelo": "RANGER",
    "versao": "Limited 3.0L V6 26MY",
    "atributos": [
      "CILINDRADA" , "POTENCIA"
    ]
  },
  "veiculo2": {
    "marca": "TOYOTA",
    "modelo": "HILUX",
    "versao": "STD Power Pack AT",
    "atributos": [
      "CILINDRADA" , "POTENCIA"
    ]
  }
}
```
# Exemplo de atualização parcial

## PATCH /api/veiculos/1
```
{
  "marca": "Toyota Atualizada"
}
```

# Aplicação disponível em
```
http://localhost:8085/api/veiculos

http://localhost:8085/swagger-ui/index.html
```

## Autenticação, usuários e avaliação

Pré-requisitos: JDK 25, MySQL ativo e Maven (ou wrapper `mvnw.cmd`). Crie o banco
`automotiva_db`; configure `DB_URL`, `DB_USER` e `DB_PASSWORD` nas variáveis de
ambiente do IntelliJ (**Run > Edit Configurations > Environment variables**).
Configure também `JWT_SECRET` com pelo menos 32 bytes aleatórios. O arquivo
`application.properties` contém valores locais de exemplo para conexão;
substitua-os conforme seu MySQL. Inicie `FichaTecnicaApplication`; Flyway aplicará
as migrações V1 a V5 em sequência. Nunca altere migrações já aplicadas.

O avaliador usa a conta `admin` criada pela migração V5, com perfil `ADMIN`
e senha inicial `admin`. A migração armazena somente o hash BCrypt. Caso o
login `admin` já existisse antes de executar a V5, a migração não altera sua
senha; nesse caso, use a senha já cadastrada. A conta `user` da V4 possui
perfil `USER`. Estas credenciais são para avaliação local: troque a senha
inicial antes de disponibilizar a API em qualquer ambiente público.

No Swagger local em `http://localhost:8085/swagger`:

1. Execute `POST /api/auth/login` com
   `{"login":"admin","senha":"admin"}`.
2. Copie o campo `token` da resposta, clique **Authorize** e cole o token.
3. Teste `POST /api/usuarios` com
   `{"login":"novo","senha":"senha-forte","roles":"USER","ativo":true}`.
4. Consulte `GET /api/usuarios` e `GET /api/usuarios/{id}`; substitua todos
   os dados com `PUT /api/usuarios/{id}` (incluindo nova senha) e exclua com
   `DELETE /api/usuarios/{id}`.

Todas as rotas de usuários exigem ADMIN. Sem token retornam 401; com token de
USER retornam 403. POST retorna 201 e Location; GET/PUT retornam 200;
DELETE retorna 204. IDs inexistentes retornam 404; login repetido retorna 409;
requisições inválidas retornam 400. Respostas nunca incluem a senha ou seu hash.
O DELETE remove o registro; a API impede que ADMIN exclua a própria conta ou
retire de si o perfil ADMIN/estado ativo. A validação do JWT consulta o banco a
cada requisição, logo excluir ou desativar um usuário invalida seu acesso.

## CORS e Swagger sem internet

Swagger e API executados em `localhost:8085` são da mesma origem. Para frontend
em outra origem, configure no IntelliJ `APP_CORS_ALLOWED_ORIGINS`, por exemplo
`http://localhost:3000,http://127.0.0.1:5173` (sem barra final). A configuração
aceita Authorization, Content-Type e Accept e os métodos GET, POST, PUT,
PATCH, DELETE e OPTIONS. Sem essa variável, origens externas não recebem
permissão CORS. O navegador faz preflight OPTIONS antes de pedidos com Bearer.

O Swagger UI é servido pelo próprio JAR `springdoc-openapi-starter-webmvc-ui`;
o navegador busca `/v3/api-docs` localmente, sem CDN. Para usar sem internet,
primeiro obtenha as dependências com Maven enquanto estiver conectado, por
exemplo `mvnw.cmd dependency:go-offline` no Windows. Depois inicie com
`mvnw.cmd -o spring-boot:run` ou execute a configuração já compilada no
IntelliJ. A página de documentação é `http://localhost:8085/swagger` e o
JSON é `http://localhost:8085/v3/api-docs`. A aplicação e o MySQL devem
permanecer acessíveis localmente.

## Arquitetura e testes

~~~mermaid
flowchart TD
    C["Cliente / Swagger"] --> S["SecurityFilterChain: CORS e acesso"]
    S --> F["Filtro JWT: assinatura, expiração e usuário ativo"]
    F --> A["Controllers: autenticação, usuários e veículos"]
    A --> B["Services: regras e BCrypt"]
    B --> R["Repositories JPA"]
    R --> D[("MySQL")]
~~~

Cliente → SecurityFilterChain (CORS, JWT e autorização) → Controller →
Service → Repository → MySQL. AuthController autentica usando BCrypt;
TokenService assina e verifica JWT de duas horas; o filtro verifica o usuário
ativo e usa o perfil atual do banco. O DTO de saída omite a senha.

No IntelliJ, abra a pasta que contém `pom.xml`, aguarde o Maven carregar e
execute `UsuarioSecurityTest`, `UsuarioServiceTest`, `VehicleSecurityTest`
e `TokenServiceTest` por **Run**; ou rode `mvnw.cmd test` no terminal
do projeto. Os novos testes cobrem 401/403, criação por ADMIN, ausência de senha
na resposta, validação 400, hash BCrypt, login duplicado e proteção da própria
conta. Registre uma captura do resultado no IntelliJ para a entrega.
