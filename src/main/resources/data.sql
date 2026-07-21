MERGE INTO genres (genre_name)
KEY (genre_name)
VALUES
  ('Комедия'),
  ('Драма'),
  ('Мультфильм'),
  ('Триллер'),
  ('Документальный'),
  ('Боевик');

MERGE INTO mpa (mpa_name)
KEY (mpa_name)
VALUES
  ('G'),
  ('PG'),
  ('PG-13'),
  ('R'),
  ('NC-17');
