
INSERT INTO genres (genre_name) VALUES
('Комедия'),
('Драма'),
('Мультфильм'),
('Триллер'),
('Документальный'),
('Боевик')
ON CONFLICT DO NOTHING;

