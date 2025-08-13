<img width="643" height="811" alt="image" src="https://github.com/user-attachments/assets/b2291bef-ac42-430f-9650-cd2cb592f803" />

This project is an MQTT-based IoT Safety Monitoring System designed to detect early warning signs such as overheating, gas leaks, and fires, and to enhance environmental safety through alerts and data logging.
Sensor data from the ET board is transmitted in real time via an MQTT broker to a Spring Boot application, where it is filtered and analyzed in the Service layer before being stored in MySQL.
The system is built on a 3-tier architecture, comprising:

- Physical Tier: Sensors, MQTT broker, and server/DB
- Application Tier: Spring Boot’s MQTT Subscriber → Service (filtering) → JPA Repository
- Presentation Tier: Web dashboard / REST API for data visualization
These tiers work seamlessly together to deliver real-time monitoring and improve safety.
