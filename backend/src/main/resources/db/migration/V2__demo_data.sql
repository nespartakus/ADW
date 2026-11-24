-- Demo seed data (Fase 1). Clearly labeled and safe to delete before production use.
-- Every record carries a "Demo" marker in its notes/title.

INSERT INTO vehicle (id, owner_id, vin, make, model, model_year, market, platform,
                     engine_code, engine_description, displacement, fuel_type, aspiration,
                     ecu, transmission, modifications, notes, created_at, updated_at, deleted_at)
VALUES
('11111111-1111-4111-8111-111111111111', NULL, NULL, 'Chevrolet', 'Silverado', 2008, 'MX', 'GMT900',
 'L59', '5.3L V8', 5.3, 'gasoline', 'natural', 'E38', 'automatic', NULL,
 'Demo — vehículo de demostración', NOW(), NOW(), NULL),
('22222222-2222-4222-8222-222222222222', NULL, NULL, 'Toyota', 'Corolla', 2019, 'MX', 'E210',
 '2ZR-FE', '1.8L I4', 1.8, 'gasoline', 'natural', 'ECM', 'CVT', NULL,
 'Demo — vehículo de demostración', NOW(), NOW(), NULL);

INSERT INTO diagnostic_session (id, vehicle_id, created_by, title, customer_reference,
                                complaint, operating_conditions, status, started_at, closed_at)
VALUES
('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa', '11111111-1111-4111-8111-111111111111', NULL,
 '[Demo] Ralentí inestable', NULL, 'Consumo elevado y P0171',
 'En caliente, a ralentí', 'OPEN', NOW(), NULL);
