CREATE DATABASE AirportSystem
COLLATE Cyrillic_General_CS_AS
GO

USE AirportSystem
GO

CREATE TABLE Accounts
(
	ID int IDENTITY(10000, 1) PRIMARY KEY,
	[login] varchar(20) NOT NULL,
	passwordHash varchar(MAX) NOT NULL,
	passwordSalt varchar(MAX) NOT NULL,
	[type] varchar(30) NOT NULL
)
GO

CREATE TABLE [Routes]
(
	ID varchar(12) NOT NULL,
	[Source] nvarchar(30) NOT NULL,
	Destination nvarchar(30) NOT NULL,
	DepartureTime varchar(5) NOT NULL,
	[Days] varchar(7) NOT NULL,
	PRIMARY KEY(ID, [Source], DepartureTime, [Days])
)
GO

CREATE TABLE RoutesInfo
(
	ID varchar(12) NOT NULL,
	[Source] nvarchar(30) NOT NULL,
	DepartureTime varchar(5) NOT NULL,
	ArrivalTime varchar(5) NULL,
	[Days] varchar(7) NOT NULL,
	PlaneType varchar(9) NOT NULL,
	[Status] varchar(20) NOT NULL,
	Gate varchar(2) NOT NULL,
	PRIMARY KEY(ID, [Source], DepartureTime, [Days])
)
GO

CREATE TABLE Planes
(
	[Type] varchar(4) PRIMARY KEY,
	[Name] varchar(MAX) NOT NULL
)
GO

CREATE TABLE PlanesInfo
(
	[Type] varchar(4) PRIMARY KEY,
	LoadID int NOT NULL, 
	Height int NOT NULL, 
	Fuel int NOT NULL,
	Capacity int NOT NULL
)
GO

CREATE TABLE [Load]
(
	LoadID int IDENTITY(0, 1) PRIMARY KEY,
	MaxLoad int NOT NULL,
	MaxTakeOff int NOT NULL, 
	MaxLanding int NOT NULL
)
GO

ALTER TABLE RoutesInfo
ADD CONSTRAINT defaultStatus DEFAULT '' FOR [Status]
GO

ALTER TABLE RoutesInfo
ADD
UNIQUE(ID, [Source], DepartureTime, [Days])
GO

ALTER TABLE RoutesInfo
ADD
FOREIGN KEY(ID, [Source], DepartureTime, [Days]) REFERENCES [Routes](ID, [Source], DepartureTime, [Days])
	ON UPDATE CASCADE ON DELETE CASCADE 
GO

ALTER TABLE RoutesInfo
ADD
FOREIGN KEY(PlaneType) REFERENCES [Planes]([Type])
	ON UPDATE CASCADE ON DELETE CASCADE
GO

ALTER TABLE PlanesInfo
ADD
FOREIGN KEY([Type]) REFERENCES Planes([Type])
	ON UPDATE CASCADE ON DELETE CASCADE
GO

ALTER TABLE PlanesInfo
ADD
FOREIGN KEY([LoadID]) REFERENCES [Load]([LoadID])
	ON UPDATE CASCADE ON DELETE CASCADE
GO