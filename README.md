# sentiment_analyzer

A backend-driven system for extracting, analyzing, and storing sentiment data from Amazon product reviews. It uses a modular microservice architecture with Kafka, Spring Boot, MongoDB, and Google Cloud Natural Language API.

---

## 🚀 Features

- Streams review data via Kafka in real-time
- Performs sentiment analysis using Google Cloud Natural Language API
- Stores and updates processed reviews in MongoDB
- Dockerized services for easy deployment and scalability

---

## 🛠️ Tech Stack
 
- **Apache Kafka** – Real-time message streaming  
- **Spring Boot** – Backend consumer service  
- **MongoDB** – NoSQL database for storing processed reviews  
- **Google Cloud Natural Language API** – Sentiment analysis  
- **Docker** – Containerized deployment

---

## 🔧 Setup Instructions

### 1. Clone the repository
```bash
git clone https://github.com/shyamcoder1123/sentiment_analyzer.git
cd sentiment_analyzer
