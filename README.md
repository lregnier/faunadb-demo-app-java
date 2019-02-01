# FaunaDB Demo App

This is a demo application which showcases how to create a simple CRUD REST service backed by FaunaDB. The examples presented in the [Getting Started](https://app.fauna.com/documentation/gettingstarted) and [CRUD](https://app.fauna.com/documentation/howto/crud) guides have been used as starting point for implementing the service.

__Table of Contents__

* [Prerequisites](#prerequisites)
* [Running the app](#running-the-app)
* [API Reference](#api-reference)
  * [Create a Post](#create-a-post)
  * [Create several Posts](#create-several-posts)
  * [Retrieve a Post](#retrieve-a-post)
  * [Retrieve Posts](#retrieve-posts)
  * [Retrieve Posts by Tags](#retrieve-posts-by-tags)
  * [Replace a Post](#replace-a-post)
  * [Delete a Post](#delete-a-post)
* [FQL Reference](#fql-reference)
  * [Save a Post](#save-a-post)
  * [Save several Posts](#save-several-posts)
  * [Find a Post](#find-a-post)
  * [Find all Posts](#find-all-posts)
  * [Find Posts by Title](#find-posts-by-title)
  * [Remove a Post](#remove-a-post)

## Prerequisites

### 1. Setup a Fauna Cloud account
Create a Fauna Cloud account filling the form [here](https://app.fauna.com/sign-up).


### 2. Install Fauna Shell
Open a terminal and install the Fauna Shell.  If you are on a PC, you can use [npm](https://www.npmjs.com/get-npm):

```
$ npm install -g fauna-shell
```

Alternatively, if you are on a Mac, you can use [Homebrew](https://brew.sh/):

```
$ brew install fauna-shell
```


### 3. Create Database
Next, create a new database to which the application will connect to for storing the data. For doing so, login to your Fauna Cloud account from the Fauna Shell by typing the following command:

```
$ fauna cloud-login
```

It will prompt for your Fauna Cloud credentials, where you need to enter the email you used for signing up, and your password.

```
Email: email@example.com
Password: **********
```

> Note: once you are logged in on a machine, the Fauna Shell will remember your credentials so you don’t need to log in again.

The next step will be to create the database. Issue the following command for creating a database called `demo-app`:


```
$ fauna create-database demo-app
```


### 4. Set up Schema

Run below command for creating the DB schema. It will execute all required queries from a file.

```
$ fauna run-queries demo-app --file=./scripts/create_schema.fql
```

### 5. Obtain an API Key
Next, issue an API Key for connecting to the new created DB from the service. Execute below command for doing so:

```
$ fauna create-key demo-app server
```

Make sure to write down the given secret key. It will be used for starting up the service later on.

> Alternatively, you can also create an API Key from the Cloud Dashboard [here](https://dashboard.fauna.com/db/demo-app/keys).

## Running the app

For starting up the service, execute below command making sure to include a proper API key as follows:

```
$ ./mvnw -Dfauna-db.secret=your_api_key_goes_here spring-boot:run
```

The app will start by default at port `8080`.

> Note: the [Maven Wrapper](https://github.com/takari/maven-wrapper) has been added to the project which allows Maven commands to be executed out of the box. Altervaintely, you can use any other Maven installation of your choice.


## API Reference

### Create a Post
Creates a new Post with an autogenerated Id.

#### Request

```
POST /posts
```

```
Content-type: application/json
{
  "title": "My cat and other marvels",
  "tags": ["pet", "cute"]
}

```

##### curl example

```
$ curl -XPOST -H "Content-type: application/json" -d '{
  "title": "My cat and other marvels",
  "tags": ["pet", "cute"]
}' 'http://localhost:8080/posts'
```

#### Response

```
Status: 201 - Created
```
```
Content-type: application/json
{
  "id": "219871526709625348",
  "title": "My cat and other marvels",
  "tags": ["pet", "cute"]
}
```

### Create several Posts
Creates several Posts within a single requests. The Posts will be persisted in a single transaction as well.

#### Request

```
POST /posts
```

```
Content-type: application/json
[
  {"title": "My cat and other marvels", "tags": ["pet", "cute"]},
  {"title": "Pondering during a commute", "tags": ["commuting"]},
  {"title": "Deep meanings in a latte", "tags": ["coffee"]}
]
```

##### curl example
```
$ curl -XPOST -H "Content-type: application/json" -d '[
  {"title": "My cat and other marvels", "tags": ["pet", "cute"]},
  {"title": "Pondering during a commute", "tags": ["commuting"]},
  {"title": "Deep meanings in a latte", "tags": ["coffee"]}
]' 'http://localhost:8080/posts'
```

#### Response

```
Status: 200 - OK
```

```
Content-type: application/json
[
  {
    "id": "219970669169869319",
    "title": "My cat and other marvels",
    "tags": ["pet", "cute"]
  },
  {
    "id": "219970865138237959",
    "title": "Pondering during a commute",
    "tags": ["commuting"]
  },
  {
    "id": "219970873639043587",
    "title": "Deep meanings in a latte",
    "tags": ["coffee"]
  }
]
```

### Retrieve a Post
Retrieves an existent Post for the given Id. If the Post cannot be found, a `404 - Not Found` response is returned.

#### Request

```
GET /posts/{post_id}
```

##### curl example
```
$ curl -XGET 'http://localhost:8080/posts/219871526709625348'
```

#### Response

```
Status: 200 - OK
```
```
Content-type: application/json
{
  "id": "219871526709625348",
  "title": "My cat and other marvels",
  "tags": ["pet", "cute"]
}
```


### Retrieve Posts
Retrieves all existent Posts.

#### Request

```
GET /posts
```

##### curl example
```
$ curl -XGET 'http://localhost:8080/posts'
```

#### Response

```
Status: 200 - OK
```

```
Content-type: application/json
[
  {
    "id": "219970669169869319",
    "title": "My cat and other marvels",
    "tags": ["pet", "cute"]
  },
  {
    "id": "219970865138237959",
    "title": "Pondering during a commute",
    "tags": ["commuting"]
  },
  {
    "id": "219970873639043587",
    "title": "Deep meanings in a latte",
    "tags": ["coffee"]
  }
]
```

### Retrieve Posts by Title
Retrieves all the existent Posts matching the given Title.

#### Request

```
GET /posts?title={post_title}
```

#### Response

```
Content-type: application/json
[
  {
    "id": "219970669169869319",
    "title": "My cat and other marvels",
    "tags": ["pet", "cute"]
  }
]
```
##### curl example

```
$ curl -XGET 'http://localhost:8080/posts?title=My%20cat%20and%20other%20marvels'
```

### Replace a Post
It replaces an existent Post for the given Id with given fields. All fields should be provided in the representation along the request. If optional fields are not provided they will be set as empty. If the Post cannot be found, a `404 - Not Found` response is returned.

#### Request

```
PUT /posts/{post_id}
```

```
Content-type: application/json
{
  "title": "My dog and other marvels"
}
```

##### curl example

```
$ curl -XPUT -H "Content-type: application/json" -d '{
  "title": "My dog and other marvels"
}' 'http://localhost:8080/posts/219871526709625348'
```

#### Response

```
Status: 200 - OK
```
```
Content-type: application/json
{
  "id": "219871526709625348"
  "title": "My dog and other marvels",
  "tags": []
}
```

> INFO: note that as the 'tags' field has not been provided in the request, it has been set as empty.


### Delete a Post
Deletes an existent Post for the given Id. If the Post cannot be found, a `404 - Not Found` response is returned.


#### Request

```
DELETE /posts/{post_id}
```

##### curl example

```
$ curl -XDELETE 'http://localhost:8080/posts/219871526709625348'
```

#### Response

```
Status: 200 - OK
```
```
Content-type: application/json
{
  "id": "219871526709625348",
  "title": "My cat and other marvels",
  "tags": ["pet", "cute"]
}
```


## FQL Reference

The persistence layer has been modeled after Domain-Driven Design Repository pattern. 

> __A word on the Repository pattern...__
> 
> Unlike DAOs, which are designed following a data access orientation, Repositories are implemented following a collection orientation. This means that their interface will mimic the one of a collection of objects rather than exposing a set of CRUD operations. The focus is put this way on the domain as a model rather than on data and any CRUD operations that may be used behind the scenes to manage the actual persistence.

### Save a Post
It creates a new a Post for the given Id with the provided data. If a Post already exists for the given Id, its data is replaced with the one supplied.

```java
Select(
  Value("data"),
  If(
    Exists(Ref(Class("posts"), Value("1520225686617873"))),
    Replace(
      Ref(Class("posts"), Value("1520225686617873")), 
      Obj("data", Obj("title", Value("My cat and other marvels")))
    ),
    Create(
      Ref(Class("posts"), Value("1520225686617873")), 
      Obj("data", Obj("title", Value("My cat and other marvels")))
    )
  )  
)
```

#### References:
* [Create](https://app.fauna.com/documentation/reference/queryapi#create)
* [Replace](https://app.fauna.com/documentation/reference/queryapi#replace)
* [Select](https://app.fauna.com/documentation/reference/queryapi#select)
* [If](https://app.fauna.com/documentation/reference/queryapi#if)
* [Exists](https://app.fauna.com/documentation/reference/queryapi#exists)

### Save several Posts
It saves several Posts within a single transaction. It uses the `Map` function to iterate over a collection of entities and apply the above save query to them.

```java
Map(
  Arr(Obj("data", Obj("title", Value("My cat and other marvels")))),
  Lambda(
    Value("nextEntity"), 
    //-- Save Query goes here
  )
```

#### References:
* [Map](https://app.fauna.com/documentation/reference/queryapi#map)
* [Lambda](https://app.fauna.com/documentation/reference/queryapi#lambda)

### Find a Post
It looks up a Post by its Id and returns its data back. 

```java
Select(
  Value("data"), 
  Get(Ref(Class("posts"), "1520225686617873"))
)
```
#### References:
* [Select](https://app.fauna.com/documentation/reference/queryapi#select)
* [Get](https://app.fauna.com/documentation/reference/queryapi#get)


### Find all Posts
It looks up all Posts in the class and returns its data back. First, all Posts Ids are found using the class `Index` together with the `Paginate` function and then its data is looked up through the `Get` function.

```java
SelectAll(
  Value("data"),
  Map(
    Paginate(Match(Index("all_posts"))),
    Lambda(Value("nextRef"), Select(Value("data"), Get(Var("nextRef"))))
  )
)
```

#### References:
* [Paginate](https://app.fauna.com/documentation/reference/queryapi#paginate)
* [Match](https://app.fauna.com/documentation/reference/queryapi#matchindexref-terms)
* [SelectAll](https://app.fauna.com/documentation/reference/queryapi#selectall)
* [Map](https://app.fauna.com/documentation/reference/queryapi#map)
* [Lambda](https://app.fauna.com/documentation/reference/queryapi#lambda)
* [Get](https://app.fauna.com/documentation/reference/queryapi#get)

### Find Posts by Title
It looks up all Posts matching the given Title and returns its data. The search is done using a previsouly created `Index`.  First, all Posts Ids are found using the `Index` together with the `Paginate` function and then its data is looked up through the `Get` function.

```java
SelectAll(
  Value("data"),
  Map(
    Paginate(Match(Index("posts_by_title"), Value("My cat and other marvels"))),
    Lambda(Value("nextRef"), Select(Value("data"), Get(Var("nextRef"))))
  )
)
```

#### References:
* [Paginate](https://app.fauna.com/documentation/reference/queryapi#paginate)
* [Match](https://app.fauna.com/documentation/reference/queryapi#matchindexref-terms)
* [SelectAll](https://app.fauna.com/documentation/reference/queryapi#selectall)
* [Map](https://app.fauna.com/documentation/reference/queryapi#map)
* [Lambda](https://app.fauna.com/documentation/reference/queryapi#lambda)
* [Get](https://app.fauna.com/documentation/reference/queryapi#get)


### Remove a Post
It removes the Post for the given Id if any and returns its data.

```java
Select(
  Value("data"),
  Delete(Ref(Class("posts"), "1520225686617873"))
)
```

#### References:
* [Delete](https://app.fauna.com/documentation/reference/queryapi#delete)
* [Select](https://app.fauna.com/documentation/reference/queryapi#select)