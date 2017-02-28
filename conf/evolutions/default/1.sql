# --- First database schema

# --- !Ups


create table account (
  id                        varchar(10) not null,
  token                     varchar(64),
  password                  varchar(255),
  constraint pk_account primary key (id))
;

create table rule (
  account_id 			    varchar(10) not null,
  short_url                 varchar(10) not null,
  long_url                  varchar(1024),
  redirect_type             int,
  redirect_count            long,
  constraint pk_rule primary key (account_id, short_url))
;

alter table rule add constraint fk_rule_account_1 foreign key (account_id) references account (id) on delete restrict on update restrict;

create unique hash index ix_rule_short_url on rule (short_url);

create unique hash index ix_account_token on account (token);


# --- !Downs




drop table if exists rule cascade;

drop table if exists account cascade;


