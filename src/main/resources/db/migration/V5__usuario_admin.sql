

INSERT INTO usuario (login, senha, roles, ativo)
SELECT
    'admin',
    '$2a$10$3//fqwEqeuIAhSlAyskxkuH8JgHs6Ud/8/YOuq/y4rw/ediAqH.MO',
    'ADMIN',
    1
WHERE NOT EXISTS (
    SELECT 1 FROM usuario WHERE login = 'admin'
);