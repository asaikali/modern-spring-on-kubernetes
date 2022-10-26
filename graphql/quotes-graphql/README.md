# quotes-graphql 

Example showing how to use SpringGrapQL 

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
  allAuthors {
      name
      wikipediaUrl
      field
  },
}

mutation {
    addAuthor(id:6, name:"Aristotle", field:POLITICS,wikipediaUrl:"https://en.wikipedia.org/wiki/Aristotle") {
        name
        wikipediaUrl
        field
    }
},
query {
    allAuthors {
        name
        wikipediaUrl
        field
    }
}
```

