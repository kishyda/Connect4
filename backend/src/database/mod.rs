use mongodb::Client;

pub async fn connect() -> mongodb::error::Result<mongodb::Database> {
    // Replace the placeholder with your Atlas connection string
    let uri = "mongodb://localhost:27017/database";
    // Create a new client and connect to the server
    let client = Client::with_uri_str(uri).await?;
    // Get a handle on the movies collection
    let database = client.database("database");
    return Ok(database);
    //let my_coll: Collection<Document> = database.collection("movies");
    //// Find a movie based on the title value
    //let my_movie = my_coll.find_one(doc! { "title": "The Perils of Pauline" }).await?;
    //// Print the document
    //println!("Found a movie:\n{:#?}", my_movie);
    //Ok(())
}
