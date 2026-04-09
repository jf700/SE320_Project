# C4 Architecture Diagrams

This directory contains the C4 model architecture diagrams for the Digital Therapy Assistant.

## Diagrams

| Level | File | Description |
|-------|------|-------------|
| 1 - Context | c4-context.puml / .png | System context with users (Patient, Therapist, Admin) and external systems (LLM, Email, EHR) |
| 2 - Container | c4-container.puml / .png | High-level building blocks: CLI, Spring Boot, H2, SimpleVectorStore, Knowledge Base |
| 3 - Component | c4-component.puml / .png | Internal components: Controllers, Services, Repositories, AI Integration, Security |
| 4 - Code | c4-code.puml / .png | Class diagram (AI Service Module) and sequence diagrams (Chat Flow, Diary Flow) |

## Generating PNGs

To regenerate PNG images from the PlantUML source files:

```bash
plantuml -tpng *.puml
```

Requires PlantUML and Java to be installed.
