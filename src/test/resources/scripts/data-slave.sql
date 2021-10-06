INSERT INTO pet_type(id, name)
VALUES (1, 'DOG_SLAVE');

INSERT INTO owner(id, first_name, last_name, address, city)
VALUES (1, 'Anton_Slave', 'Ivanov_Slave', 'Spring 10, 25', 'Samara');

INSERT INTO pet(id, identification_number, owner_id, type_id)
VALUES (1, 'DOG', 1, 1);

INSERT INTO visit(pet_id, visit_start, visit_end, description)
values (1, now(), now(), 'Some description');
