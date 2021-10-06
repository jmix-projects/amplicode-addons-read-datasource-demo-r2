INSERT INTO pet_type(name)
VALUES ('DOG');

INSERT INTO owner(first_name, last_name, address, city)
VALUES ('Anton', 'Ivanov', 'Spring 10, 25','Samara');

INSERT INTO pet(identification_number, owner_id, type_id)
VALUES ('DOG_1', 1, 1);

INSERT INTO visit(pet_id, visit_start, visit_end, description)
values (1, now(), now(), 'Some description');
