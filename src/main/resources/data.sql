--USERS
insert into users (id, email, first_name, last_name, password)
values ('ba804cb9-fa14-42a5-afaf-be488742fc54', 'admin@example.com', 'James', 'Bond',
        '$2a$10$TM3PAYG3b.H98cbRrHqWa.BM7YyCqV92e/kUTBfj85AjayxGZU7d6'), -- Password: 1234
       ('0d8fa44c-54fd-4cd0-ace9-2a7da57992de', 'user@example.com', 'Tyler', 'Durden',
        '$2a$10$TM3PAYG3b.H98cbRrHqWa.BM7YyCqV92e/kUTBfj85AjayxGZU7d6')  -- Password: 1234
    ON CONFLICT DO NOTHING;


--ROLES
INSERT INTO role(id, name)
VALUES ('d29e709c-0ff1-4f4c-a7ef-09f656c390f1', 'DEFAULT'),
       ('ab505c92-7280-49fd-a7de-258e618df074', 'ADMIN'),
       ('c6aee32d-8c35-4481-8b3e-a876a39b0c02', 'USER') ON CONFLICT DO NOTHING;

--AUTHORITIES
INSERT INTO authority(id, name)
VALUES ('2ebf301e-6c61-4076-98e3-2a38b31daf86', 'DEFAULT'),
       ('76d2cbf6-5845-470e-ad5f-2edb9e09a868', 'USER_MODIFY'),
       ('21c942db-a275-43f8-bdd6-d048c21bf5ab', 'USER_DELETE') ON CONFLICT DO NOTHING;

--assign roles to users
insert into users_role (users_id, role_id)
values ('ba804cb9-fa14-42a5-afaf-be488742fc54', 'd29e709c-0ff1-4f4c-a7ef-09f656c390f1'),
       ('0d8fa44c-54fd-4cd0-ace9-2a7da57992de', 'd29e709c-0ff1-4f4c-a7ef-09f656c390f1'),
       ('ba804cb9-fa14-42a5-afaf-be488742fc54', 'ab505c92-7280-49fd-a7de-258e618df074'),
       ('ba804cb9-fa14-42a5-afaf-be488742fc54', 'c6aee32d-8c35-4481-8b3e-a876a39b0c02') ON CONFLICT DO NOTHING;

--assign authorities to roles
INSERT INTO role_authority(role_id, authority_id)
VALUES ('d29e709c-0ff1-4f4c-a7ef-09f656c390f1', '2ebf301e-6c61-4076-98e3-2a38b31daf86'),
       ('ab505c92-7280-49fd-a7de-258e618df074', '76d2cbf6-5845-470e-ad5f-2edb9e09a868'),
       ('c6aee32d-8c35-4481-8b3e-a876a39b0c02', '21c942db-a275-43f8-bdd6-d048c21bf5ab') ON CONFLICT DO NOTHING;

--EVENTS
INSERT INTO events (id, event_creator, event_name, date, location)
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'ba804cb9-fa14-42a5-afaf-be488742fc54','Spring Boot Conference', '2025-06-15', 'New York'),
       ('550e8400-e29b-41d4-a716-446655440001', 'ba804cb9-fa14-42a5-afaf-be488742fc54','Java Dev Meetup', '2025-07-20', 'San Francisco'),
       ('550e8400-e29b-41d4-a716-446655440002', 'ba804cb9-fa14-42a5-afaf-be488742fc54',' Tech Hackathon', '2025-08-10', 'Seattle');

INSERT INTO events_guests (events_id, users_id)
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'ba804cb9-fa14-42a5-afaf-be488742fc54'), -- James at Spring Boot                                      -- John at Spring Boot Conference
       ('550e8400-e29b-41d4-a716-446655440001', 'ba804cb9-fa14-42a5-afaf-be488742fc54'), -- James at Java Dev Meetup
       ('550e8400-e29b-41d4-a716-446655440001', '0d8fa44c-54fd-4cd0-ace9-2a7da57992de'), -- Tyler at Java Dev Meetup
       ('550e8400-e29b-41d4-a716-446655440002', '0d8fa44c-54fd-4cd0-ace9-2a7da57992de'); -- Tyler at Tech Hackathon



