drop table Customer cascade constraints;
drop table Account cascade constraints;
drop table PhoneNumber cascade constraints;
drop table contains;
drop table BillingPlan cascade constraints;
drop table soldPhone cascade constraints;
drop table Activity;
drop table store cascade constraints;
drop table stock;
drop table usageInternet;
drop table usageText;
drop table usageCalls;
drop table onlineStore cascade constraints;
drop table purchased;
drop sequence Customer_seq;
drop sequence account_seq;
drop sequence online_seq;
drop sequence store_seq;
drop sequence call_seq;
drop sequence text_seq;
drop sequence internet_seq;

create table Customer(
	id number(10,0) not null,
	name varchar(40),
	street_address varchar(40),
	city varchar(20),
	zipcode varchar(5),
	state varchar(2),
	primary key(id)
);

create table BillingPlan(
	billing_name varchar(10) not null,
	monthly_rate number(6,2),
	per_call_min number(6,2),
	per_text number(6,2),
	per_byte number(6,4),
	primary key(billing_name)
);
create table onlineStore(
	Model_ID number(10,0),
	Manufacturer varchar(30),
	model varchar(15),
	price number(5,2),
	primary key(Model_id)
);


create table SoldPhone(
	MEID varchar(56) ,
	model_id number,
	FOREIGN key(Model_id) REFERENCES onlineStore(Model_id) on delete set null,
	active timestamp(6),
	primary key(MEID)
);

create table phoneNumber(
	phone_number varchar(10),
	MEID varchar(56),
	active number(1,0),
	foreign key(MEID) references soldPhone(MEID)on delete set null,
	primary key(phone_number)
);

create table Account(
	A_id number(10,0) not null,
	id number(10,0),
	foreign key(id) references Customer(id) on delete cascade,
	type varchar(15),
	phone_number varchar(10),
	foreign key(phone_number) references phoneNumber(phone_number) on delete set null,
	billing_name varchar(10),
	foreign key(Billing_name) references billingPlan(billing_name) on delete set null,
	primary key(a_id)
);

create table contains(
	a_id number,
	foreign key(a_id) references Account(a_id) on delete cascade,
	phone_number varchar(10),
	foreign key(phone_number) references phoneNumber(phone_number) on delete cascade
);

create table Activity(
	MEID varchar(56),
	foreign key(MEID) references soldPhone(MEID) on delete cascade,
	phone_number varchar(10),
	foreign key(phone_number) references PhoneNumber(phone_number) on delete cascade,
	start_time Timestamp(6),
	end_Time Timestamp(6)
);

create table Store(
	s_id number(10,0),
	street_address varchar(40),
	city varchar(20),
	zipcode numeric(5),
	state varchar(2),
	primary key(s_id)
);


create table stock(
	model_id number(10,0),
	FOREIGN key(Model_id) REFERENCES onlineStore(Model_id) on delete cascade,
	s_id number(10,0),
	foreign key(s_id) references Store(s_id),
	quantity number(3,0)
);

create table usageInternet(
	i_id number(10,0),
	phone_number varchar(10),
	foreign key(phone_number) references PhoneNumber(phone_number) on delete set null,
	bytes number(15,0) default 0,
	year number(4,0),
	month number(2,0),
	primary key(i_id)
);

create table usageCalls(
	c_id number(10.0),
	start_time timestamp(6),
	duration_sec number(10,0),
	destination_number varchar(10) not null,
	phone_number varchar(10),
	foreign key(phone_number) references PhoneNumber(phone_number) on delete set null
);

create table usageText(
	t_id number(10,0),
	time timestamp(6),
	destination_number varchar(10) not null,
	phone_number varchar(10),
	foreign key(phone_number) references PhoneNumber(phone_number) on delete set null
);

create table purchased(
	a_id number,
	foreign key(a_id) references Account(a_id) on delete cascade,
	model_id number(10,0),
	FOREIGN key(Model_id) REFERENCES onlineStore(Model_id) on delete cascade,
	time timestamp(6)
);

create table BillingStatement{
	a_id number,
	foreign key(a_id) references Account(a_id) on delete cascade,
	billing_name varchar(10),
	foreign key(Billing_name) references billingPlan(billing_name) on delete set null,
	month number(1,0),
	year number(4,0),
	total number(9,2);
}

create sequence customer_seq;
create sequence account_seq;
create sequence online_seq;
create sequence store_seq;
create sequence call_seq;
create sequence text_seq;
create sequence internet_seq;

