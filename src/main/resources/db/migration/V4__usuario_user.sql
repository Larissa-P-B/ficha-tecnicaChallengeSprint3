
INSERT INTO usuario (login, senha, roles, ativo)
SELECT
    'user',
    '$2a$10$G1AsXZlMbk/BnaWrMDHLNeww7y/u1ArQ/6tw/eKy7nmw1GdV4Gjhm',
    'USER',
    1
    WHERE NOT EXISTS (
    SELECT 1 FROM usuario WHERE login = 'user'
);