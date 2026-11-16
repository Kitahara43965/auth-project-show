(1) git clone https://github.com/Kitahara43965/auth-project-show.git<br>
(2) cd auth-project-show<br>
(3-1) docker rm backend-postgres<br>
(3-2) docker compose up -d --build<br>
(4) .env_exampleをcopyして.envに変更<br>
(5) npm install<br>
(6) http://localhost:3000/loginにログイン
