# quotes-graphql 

Example showing how to use SpringGraphQL

## Things to try out 

1. Run the application 
2. Go to [http://localhost:8080/graphiql](http://localhost:8080/graphiql) and 
   try out the UI
3. Execute the query below 

```graphql
  allAuthors {
      name
      wikipediaUrl
      field
  }
```

4. Execute the query 
```graphql
{
  randomQuote {
    id,
    quote,
    author {
      name
      wikipediaUrl
      field
    }
  }
}
```

5. Execute the query 

```graphql
mutation {
    addAuthor(id:6, name:"Aristotle", field:POLITICS,wikipediaUrl:"https://en.wikipedia.org/wiki/Aristotle") {
        name
        wikipediaUrl
        field
    }
}
```

6. Execute the query
```graphql
query {
    allAuthors {
        name
        wikipediaUrl
        field
    }
}
```
5. Inspect the code in `QuoteController` follow the code to see how the GarphQL 
   end points are implemented 

6. Check the code in the test directory 
