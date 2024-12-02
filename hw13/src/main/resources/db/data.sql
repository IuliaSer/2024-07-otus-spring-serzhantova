insert into authors(full_name)
values ('Author_1'), ('Author_2'), ('Author_3');

insert into genres(name)
values ('Genre_1'), ('Genre_2'), ('Genre_3');

insert into books(title, author_id, genre_id)
values ('BookTitle_1', 1, 1), ('BookTitle_2', 2, 2), ('BookTitle_3', 3, 3);

insert into comments(message, book_id)
values ('Message_1', 1), ('Message_2', 2), ('Message_3', 3);

insert into users(login, password, role)
values ('User_1', '$2a$12$qrFJ/ZHdJRu6uuO8Gv91BOMsYkiuCKrj.AnmqApy.GOzNNtlrN/we', 'ROLE_ADMIN'),
       ('User_2', '$2a$12$Eluio.oMtdmdf10pK8ovMeFHR87I95xiEesHjkOiuGSlAksQleTYO', 'ROLE_USER');