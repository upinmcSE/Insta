
<!-- LOGO -->
<h1 align="center">Insta</h1>
<div align="center">
  <img src="docs/logo.png" alt="Logo" height=80 width=80 />
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#license">License</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->
## About The Project
This is a full-stack social media web application built with React.js for the frontend and Spring Boot for the backend. The platform provides essential features for user interaction and content sharing, including:

- User authentication and registration, supporting both traditional email/password and Google OAuth login.

- A guided onboarding process for new users after login.

- Users can create posts with images and captions, and engage by liking or commenting on others' posts.

- A follow/unfollow system allows users to manage their social network.

- A personalized newsfeed displays posts from followed users to ensure a smooth and relevant browsing experience.

- A built-in chat system enables real-time messaging between users.

This project showcases my ability to integrate frontend and backend technologies to build a feature-rich, interactive web application with a strong focus on user experience and modern development practices.

<!-- Demo Images Grid -->
<div align="center">
  <table>
    <tr>
      <td align="center">
        <img src="docs/login.png" alt="Demo 1" width="300" height="200" />
      </td>
      <td align="center">
        <img src="docs/register.png" alt="Demo 2" width="300" height="200" />
      </td>
      <td align="center">
        <img src="docs/home.png" alt="Demo 3" width="300" height="200" />
      </td>
    </tr>
    <tr>
      <td align="center">
        <img src="docs/create.png" alt="Demo 4" width="300" height="200" />
      </td>
      <td align="center">
        <img src="docs/profile.png" alt="Demo 5" width="300" height="200" />
      </td>
      <td align="center">
        <img src="docs/chat.png" alt="Demo 6" width="300" height="200" />
      </td>
    </tr>
  </table>
</div>

<!-- Project Description -->


### Built With

<div align="center">
  <table>
    <tr>
      <td align="center" width="120">
        <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot" />
      </td>
      <td align="center" width="120">
        <img src="https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB" alt="React" />
      </td>
      <td align="center" width="120">
        <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" />
      </td>
    </tr>
    <tr>
      <td align="center" width="120">
        <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis" />
      </td>
      <td align="center" width="120">
        <img src="https://img.shields.io/badge/MinIO-C72E49?style=for-the-badge&logo=minio&logoColor=white" alt="MinIO" />
      </td>
      <td align="center" width="120">
        <img src="https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white" alt="RabbitMQ" />
      </td>
    </tr>
    <tr>
      <td align="center" colspan="3">
        <img src="https://img.shields.io/badge/WebSocket-000000?style=for-the-badge&logo=websocket&logoColor=white" alt="WebSocket" />
      </td>
    </tr>
  </table>
</div>

## Getting Started

### Prerequisites
- Java 17+
- MySQL 8+
- Node.js 18+
- Redis 7+
- Docker (optional, for containerization)

### Installation
1. Clone the repo
   ```sh
   git clone https://github.com/upinmcSE/Insta.git
   ```
2. Navigate to the frontend directory to install dependencies and run
    ```sh
      cd frontend
      npm install
      npm run dev
    ```
3. Start the required services using Docker Compose  
   ```sh
      cd docker
      docker-compose up -d
   ```
4. Run the Spring Boot backend application
   ```sh
      cd backend
      ./mvnw spring-boot:run
   ```

## Roadmap
- [x] Users can create, login 
- [x] Users can update profile
- [x] Users can create post with images and caption
- [x] Users can view newfeed
- [x] Users can like and comment of others
- [x] Users can search for the people they want
- [x] Users can chat with each other
- [x] Users can receive notifications when someone likes and comments on their posts.
- [ ] Better UI/UX improvements
- [ ] Add more features upload video, reels , etc.


## License
[MIT](https://choosealicense.com/licenses/mit/)



