

CREATE TABLE IF NOT EXISTS users (
  user_id SERIAL PRIMARY KEY,
  email VARCHAR(200),
  login VARCHAR(200),
  user_name VARCHAR(200),
  birthday TIMESTAMP
);

CREATE TABLE IF NOT EXISTS films (
  film_id SERIAL PRIMARY KEY,
  title VARCHAR(50),
  description VARCHAR(200),
  release_date DATE,
  duration INTEGER,
  rating VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS genres (
  genre_id SERIAL PRIMARY KEY,
  genre_name VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS likes (
  like_id SERIAL PRIMARY KEY,
  film_id INTEGER,
  user_id INTEGER,
  FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS film_genres (
  film_genres_id SERIAL PRIMARY KEY,
  film_id INTEGER,
  genres_id INTEGER,
  FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
  FOREIGN KEY (genres_id) REFERENCES genres (genre_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_friends (
  user_friends_id SERIAL PRIMARY KEY,
  user_id INTEGER,
  friend_user_id INTEGER,
  FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
  FOREIGN KEY (friend_user_id) REFERENCES users (user_id) ON DELETE CASCADE
);
