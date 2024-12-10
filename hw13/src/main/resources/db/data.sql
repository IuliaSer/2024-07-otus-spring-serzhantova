insert into authors(full_name)
values ('Author_1'), ('Author_2'), ('Author_3');

insert into genres(name)
values ('Genre_1'), ('Genre_2'), ('Genre_3');

insert into books(title, author_id, genre_id)
values ('BookTitle_1', 1, 1), ('BookTitle_2', 2, 2), ('ForAdults', 3, 3);

insert into comments(message, book_id)
values ('Message_1', 1), ('Message_2', 2), ('Message_3', 3);

insert into users(login, password, authority)
values ('User_1', '$2a$12$qrFJ/ZHdJRu6uuO8Gv91BOMsYkiuCKrj.AnmqApy.GOzNNtlrN/we', 'ROLE_ADMIN'),
       ('User_2', '$2a$12$Eluio.oMtdmdf10pK8ovMeFHR87I95xiEesHjkOiuGSlAksQleTYO', 'ROLE_USER'),
       ('User_3', '$2a$12$Z51JpTESamd3fSsiTQ2fYujhRvlNr3rttbjOJ/E0q0Lnb3/Ta7Oau', 'ROLE_USER');

INSERT INTO acl_sid (id, principal, sid) VALUES
(1, 1, 'User_1'),
(2, 1, 'User_2'),
(3, 1, 'User_3'),
(4, 0, 'ROLE_ADMIN');

INSERT INTO acl_class (id, class) VALUES
(1, 'ru.otus.hw.dto.ShortBookDto');

INSERT INTO acl_object_identity (id, object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting) VALUES
(1, 1, 1, NULL, 3, 0),
(2, 1, 2, NULL, 3, 0),
(3, 1, 3, NULL, 3, 0);

INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) VALUES
(1, 1, 4, 1, 2, 1, 1, 1),
(2, 1, 2, 2, 2, 1, 1, 1),
(3, 1, 3, 3, 2, 1, 1, 1),
(4, 1, 1, 4, 2, 1, 1, 1),
(5, 2, 3, 1, 2, 1, 1, 1),
(6, 2, 2, 2, 2, 1, 1, 1),
(7, 2, 1, 4, 2, 1, 1, 1),
(8, 3, 4, 1, 2, 1, 1, 1),
(9, 3, 2, 2, 2, 1, 1, 1),
(10, 3, 3, 3, 2, 1, 1, 1),
(11, 3, 1, 4, 2, 1, 1, 1);