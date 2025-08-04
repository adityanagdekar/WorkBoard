# 🧩 WorkBoard
A full-stack project tracking app with drag-and-drop task boards, secure access control, and live UI updates.

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Highlights](#project-highlights)
- [Future Enhancements](#future-enhancements)

## Overview
WorkBoard is a web-based Kanban board that allows users to organize, prioritize, and collaborate on tasks in real-time. It supports user authentication, board creation, task assignment, and dynamic status updates using drag-and-drop.

## Features
- ✅ Secure login with JWT-based session management
- 🧑‍💼 Role-based access control (Manager vs Member)
- 🗂️ Boards with customizable lists and task cards
- ⏱️ Real-time drag-and-drop with debounce + backend syncing
- 📝 Task editing, and member assignment
- 🔁 Activity logs and state preservation

## Tech Stack
- 🔙 Backend: Java Spring Boot, Spring Security, PostgreSQL
- 🔚 API: RESTful endpoints with @Transactional services
- 🖥️ Frontend: React.js (Vite)
- 🧪 Tools: Postman (testing), GitHub (CI)

## Project Highlights
- Designed REST APIs using @RestController and Spring Data JPA with @OneToMany, @IdClass
- Implemented transaction-safe updates using @Transactional for board and task edits
- Built session-based role management using Spring Security
- Developed reusable React components and debounced backend sync logic

## Future Enhancements
- ✅ Real-time updates via WebSockets
- ✅ Full-text search across boards and tasks
- ✅ AI summarizer (via Ollama or local LLM server)
- ✅ Task analytics and progress heatmaps
