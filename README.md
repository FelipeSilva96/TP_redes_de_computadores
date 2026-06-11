# 🌐 Trabalho Prático — Redes de Computadores I

<p align="center">
  <strong>Aplicação distribuída em Java com TCP, UDP, Java Swing e multithreading</strong>
</p>

<p align="center">
  <img alt="Status" src="https://img.shields.io/badge/status-estrutura%20inicial-blue">
  <img alt="Java" src="https://img.shields.io/badge/Java-planejado-orange">
  <img alt="TCP" src="https://img.shields.io/badge/TCP-obrigatório-1f6feb">
  <img alt="UDP" src="https://img.shields.io/badge/UDP-obrigatório-6f42c1">
  <img alt="Swing" src="https://img.shields.io/badge/Java%20Swing-interface-2ea44f">
  <img alt="Roteadores" src="https://img.shields.io/badge/topologia-2%20roteadores-yellow">
</p>

---

## 📌 Sobre o projeto

Este repositório será utilizado no desenvolvimento do **Trabalho Prático da disciplina Redes de Computadores I**, da PUC Minas.

O objetivo geral é construir uma **aplicação em rede utilizando Java**, composta por um cliente e um servidor executados em computadores diferentes. A comunicação atravessará uma topologia formada por **dois roteadores**, permitindo exercitar, de forma integrada, conceitos como:

- arquitetura cliente-servidor;
- endereçamento IPv4 e sub-redes;
- protocolos de transporte **TCP** e **UDP**;
- portas e sockets;
- multiplexação e demultiplexação;
- NAT e redirecionamento de portas;
- comunicação entre redes distintas;
- concorrência com **multithreading**;
- captura e análise de tráfego com **Wireshark**;
- modelagem e validação da topologia no **Cisco Packet Tracer**.

> **Estado atual:** o repositório contém somente a estrutura inicial de diretórios e a documentação de planejamento. A implementação da aplicação será realizada nas próximas etapas.

---

## ✅ Requisitos do trabalho

A solução deverá atender aos seguintes requisitos acadêmicos:

- [ ] ser desenvolvida em **Java**;
- [ ] possuir uma aplicação **cliente com interface gráfica Java Swing**;
- [ ] possuir pelo menos uma funcionalidade cuja natureza demande **TCP**;
- [ ] possuir pelo menos uma funcionalidade cuja natureza demande **UDP**;
- [ ] utilizar **multithreading**;
- [ ] executar cliente e servidor em computadores diferentes;
- [ ] utilizar uma topologia física/lógica com **dois roteadores**;
- [ ] configurar o encaminhamento das portas necessárias;
- [ ] representar a rede no **Cisco Packet Tracer**;
- [ ] capturar e analisar as comunicações no **Wireshark**;
- [ ] produzir relatório técnico com as principais configurações, telas e resultados;
- [ ] apresentar o funcionamento do projeto presencialmente.

Esses requisitos derivam diretamente do enunciado, que exige Java, cliente Swing, funcionalidades TCP e UDP, multithreading, projeto no Packet Tracer, análise no Wireshark e relatório com as configurações. fileciteturn11file0

---

## 🗺️ Topologia adaptada para dois roteadores

O enunciado admite o uso de três roteadores ou, eventualmente, dois. Neste projeto será adotada a versão com **dois roteadores**.

```text
┌───────────────────┐      Wi-Fi      ┌────────────┐      Cabo RJ45      ┌────────────┐      Wi-Fi      ┌───────────────────┐
│ PC1               │ ──────────────> │ Roteador 1 │ ─────────────────> │ Roteador 2 │ ──────────────> │ PC2               │
│ Cliente Java      │                 │ R1         │                    │ R2         │                 │ Servidor Java     │
│ Interface Swing   │                 │ Rede 1     │                    │ Rede 2     │                 │ TCP e UDP         │
└───────────────────┘                 └────────────┘                    └────────────┘                 └───────────────────┘
```

### Organização lógica prevista

| Elemento | Função prevista |
|---|---|
| **PC1** | Executar o cliente Java com interface Swing |
| **R1** | Fornecer a primeira rede e encaminhar o tráfego destinado ao servidor para R2 |
| **R2** | Fornecer a segunda rede e encaminhar as portas TCP/UDP para o PC2 |
| **PC2** | Executar o servidor e atender as requisições do cliente |

### Plano de endereçamento inicial

A distribuição abaixo é uma proposta de referência e poderá ser refinada durante a configuração prática:

| Segmento | Faixa de referência | Observação |
|---|---|---|
| **LAN de R1** | `192.168.0.0/16` | Rede do PC1 e da interface WAN de R2 |
| **LAN de R2** | `172.16.0.0/12` | Rede do PC2/servidor |

A rede `10.0.0.0/8`, prevista na topologia original de três roteadores, não será necessária na adaptação adotada neste projeto.

---

## 🔀 Fluxo de comunicação e redirecionamento de portas

A comunicação será organizada de modo que o cliente em PC1 consiga alcançar o servidor em PC2 mesmo estando em outra rede e atrás de dois roteadores.

Fluxo previsto:

1. o servidor será iniciado no **PC2**, escutando as portas definidas para TCP e UDP;
2. em **R2**, serão criadas regras de port forwarding para encaminhar as portas externas até o endereço privado do PC2;
3. em **R1**, serão criadas regras equivalentes apontando para o endereço WAN de R2;
4. o cliente em **PC1** enviará suas requisições ao ponto de entrada configurado em R1;
5. o tráfego atravessará R1 e R2 até chegar ao servidor;
6. as respostas retornarão ao cliente pelas traduções e associações mantidas pelos roteadores.

> TCP e UDP poderão exigir regras separadas de encaminhamento, mesmo quando utilizarem o mesmo número de porta, pois são protocolos de transporte distintos.

---

## 🧩 Arquitetura planejada da aplicação

### Cliente

Responsável por:

- apresentar a interface gráfica em Java Swing;
- coletar os dados fornecidos pelo usuário;
- iniciar requisições TCP e UDP;
- exibir respostas e informações de estado;
- manter a interface responsiva durante operações de rede.

### Servidor

Responsável por:

- escutar portas TCP e UDP;
- receber e interpretar requisições;
- executar as funcionalidades da aplicação;
- responder ao cliente;
- atender múltiplas operações ou clientes com apoio de threads.

### Componentes compartilhados

Responsáveis por concentrar estruturas reutilizáveis, como:

- modelos de mensagem;
- constantes de protocolo;
- validações;
- utilitários de serialização;
- códigos de operação e resposta.

---

## 🚚 Uso planejado de TCP e UDP

A aplicação deverá conter funcionalidades que **justifiquem tecnicamente** a escolha de cada protocolo.

### TCP

Será utilizado em uma funcionalidade que demande:

- estabelecimento de conexão;
- entrega confiável;
- preservação da ordem dos dados;
- retransmissão em caso de perda;
- controle de fluxo e congestionamento.

### UDP

Será utilizado em uma funcionalidade que valorize:

- baixa sobrecarga;
- comunicação direta por datagramas;
- baixa latência;
- troca simples de mensagens;
- tolerância a perdas ou confirmação implementada pela própria aplicação.

As funcionalidades definitivas serão documentadas quando o tema operacional da aplicação for consolidado.

---

## 🧵 Multithreading

O projeto deverá usar concorrência para evitar bloqueios e permitir que diferentes atividades ocorram simultaneamente.

Usos previstos:

- execução dos servidores TCP e UDP ao mesmo tempo;
- atendimento de múltiplos clientes ou requisições;
- separação entre processamento de rede e interface Swing;
- prevenção de travamentos da Event Dispatch Thread;
- gerenciamento independente de envio, recebimento e processamento.

---

## 📁 Estrutura do repositório

```text
TP_redes_de_computadores/
├── app/
│   ├── client/
│   │   └── src/
│   │       ├── main/
│   │       │   ├── java/
│   │       │   └── resources/
│   │       └── test/
│   │           └── java/
│   ├── server/
│   │   └── src/
│   │       ├── main/
│   │       │   ├── java/
│   │       │   └── resources/
│   │       └── test/
│   │           └── java/
│   └── shared/
│       └── src/
│           └── main/
│               └── java/
├── network/
│   ├── addressing/
│   ├── packet-tracer/
│   └── router-configs/
│       ├── r1/
│       └── r2/
├── captures/
│   └── wireshark/
│       ├── tcp/
│       └── udp/
├── docs/
│   ├── report/
│   │   └── images/
│   │       ├── application/
│   │       ├── routers/
│   │       └── wireshark/
│   └── presentation/
├── .gitignore
└── README.md
```

### Finalidade das pastas

| Diretório | Finalidade |
|---|---|
| `app/client` | Código-fonte, recursos e testes do cliente Swing |
| `app/server` | Código-fonte, recursos e testes do servidor |
| `app/shared` | Classes e estruturas compartilhadas entre cliente e servidor |
| `network/addressing` | Plano de endereçamento, tabelas de IP, portas e observações de configuração |
| `network/packet-tracer` | Arquivo `.pkt` da topologia simulada |
| `network/router-configs` | Registros e exportações das configurações de R1 e R2 |
| `captures/wireshark/tcp` | Capturas `.pcap`/`.pcapng` e evidências da comunicação TCP |
| `captures/wireshark/udp` | Capturas `.pcap`/`.pcapng` e evidências da comunicação UDP |
| `docs/report` | Conteúdo e recursos utilizados na elaboração do relatório final |
| `docs/presentation` | Arquivos da apresentação presencial |

Os arquivos `.gitkeep` existem apenas para permitir que o Git preserve diretórios ainda vazios.

---

## 🔬 Validação prevista

### Aplicação

- verificar inicialização do cliente e do servidor;
- testar as funcionalidades TCP e UDP;
- confirmar tratamento de erros e indisponibilidade;
- avaliar atendimento concorrente;
- verificar responsividade da interface Swing.

### Rede

- validar endereçamento e conectividade entre os equipamentos;
- testar as regras de redirecionamento de portas;
- confirmar o caminho do tráfego pelos dois roteadores;
- comparar a topologia física com a representação no Packet Tracer.

### Wireshark

- identificar handshakes e encerramentos TCP;
- analisar endereços IP e portas de origem/destino;
- observar datagramas UDP;
- aplicar filtros de exibição;
- registrar evidências para o relatório.

---

## 📦 Entregáveis previstos

- aplicação Java cliente-servidor;
- interface cliente em Java Swing;
- funcionalidades TCP e UDP;
- uso demonstrável de multithreading;
- projeto do Cisco Packet Tracer;
- configurações dos dois roteadores;
- capturas e análises do Wireshark;
- relatório técnico em PDF;
- apresentação presencial.

---

## 🛣️ Roadmap

### Etapa 1 — Preparação

- [x] criar o repositório;
- [x] definir a estrutura inicial de diretórios;
- [x] documentar o escopo e a topologia adaptada;
- [ ] definir o tema e as funcionalidades da aplicação;
- [ ] escolher portas TCP e UDP.

### Etapa 2 — Projeto da rede

- [ ] definir o plano final de endereçamento;
- [ ] configurar R1 e R2;
- [ ] configurar DHCP ou endereços estáticos;
- [ ] configurar port forwarding TCP e UDP;
- [ ] construir a topologia no Packet Tracer.

### Etapa 3 — Desenvolvimento

- [ ] implementar o servidor TCP;
- [ ] implementar o servidor UDP;
- [ ] implementar o cliente Swing;
- [ ] implementar multithreading;
- [ ] implementar validações e tratamento de erros;
- [ ] criar testes básicos.

### Etapa 4 — Integração e testes

- [ ] executar cliente e servidor em computadores distintos;
- [ ] testar o tráfego atravessando os dois roteadores;
- [ ] capturar as comunicações no Wireshark;
- [ ] validar resultados TCP e UDP;
- [ ] registrar telas e evidências.

### Etapa 5 — Entrega

- [ ] finalizar o relatório;
- [ ] inserir imagens dos roteadores, aplicação e Wireshark;
- [ ] revisar o arquivo do Packet Tracer;
- [ ] preparar a apresentação presencial;
- [ ] revisar todos os artefatos antes da entrega.

---

## 🛠️ Tecnologias e ferramentas previstas

| Tecnologia | Uso no projeto |
|---|---|
| **Java** | Implementação do cliente e servidor |
| **Java Swing** | Interface gráfica do cliente |
| **TCP** | Comunicação confiável e orientada à conexão |
| **UDP** | Comunicação baseada em datagramas |
| **Threads Java** | Concorrência e responsividade |
| **Cisco Packet Tracer** | Modelagem da topologia |
| **Wireshark** | Captura e análise dos pacotes |
| **Git/GitHub** | Versionamento e colaboração |

---

## ▶️ Execução

Os comandos de compilação e execução serão adicionados após a definição da estrutura de build e a implementação dos módulos.

Futuramente, esta seção documentará:

- pré-requisitos;
- versão mínima do Java;
- configuração de IPs e portas;
- inicialização do servidor;
- inicialização do cliente;
- execução dos testes;
- filtros recomendados no Wireshark.

---

## 👨‍💻 Autoria

Projeto acadêmico desenvolvido para a disciplina **Redes de Computadores I — PUC Minas**.

- **Felipe Silva** — [GitHub](https://github.com/FelipeSilva96)

---

## 📚 Observação acadêmica

Este repositório documentará tanto a implementação da aplicação quanto a configuração e análise da infraestrutura de rede. O foco não será apenas fazer cliente e servidor se comunicarem, mas também demonstrar, de forma prática, como os protocolos de transporte, o endereçamento IP, o NAT, o redirecionamento de portas e a análise de pacotes se relacionam em uma comunicação real entre redes distintas.
