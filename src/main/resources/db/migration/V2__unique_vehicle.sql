ALTER TABLE vehicle
ADD CONSTRAINT uk_vehicle
UNIQUE (marca, modelo, versao);