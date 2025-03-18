use axum::{routing::get, routing::any, Router};

mod database;
mod game;

use std::sync::Arc;

use axum::{extract::State, routing::get, Router};

pub type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

fn corslayer() -> tower_http::cors::CorsLayer {
    tower_http::cors::CorsLayer::new()
        .allow_origin(tower_http::cors::Any)
        .allow_credentials(true)
        .allow_origin(
            "http://localhost:5173"
                .parse::<http::HeaderValue>()
                .unwrap(),
        )
        .allow_methods(tower_http::cors::Any)
}

#[tokio::main]
async fn main() {
    let database = std::sync::Arc::new(database::connect().await.unwrap());

    let app = Router::new()
        .route("/", get(|| async { "Hello, World!" }))
        .route("/kadjf", get(|| async {}))
            .layer(corslayer())
        .route("/testing", get(handler))
            .layer(corslayer())
            .with_state(database.clone())
        .route("/testing2", get(handler2))
            .layer(corslayer())
            .with_state(database.clone())
        .route("/ws", any());

    let listener = tokio::net::TcpListener::bind("0.0.0.0:3000").await.unwrap();
    axum::serve(listener, app).await.unwrap();
}

async fn handler(State(state): State<Arc<mongodb::Database>>) {}

// Another handler
async fn handler2(State(state): State<Arc<mongodb::Database>>) {
    // Read from the database in another handler
}
