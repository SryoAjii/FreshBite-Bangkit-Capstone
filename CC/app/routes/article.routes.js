const controller = require("../controllers/articleController");

module.exports = function(app){
    app.use(function(req, res, next) {
        res.header(
          "Access-Control-Allow-Headers",
          "Origin, Content-Type, Accept"
        );
        next();
    });

    app.post(
        "/api/articles",
        controller.createArticle
    );
    
    app.get(
        "/api/articles",
        controller.getArticles
    );

    app.get(
        "/api/articles/:id", 
        controller.getArticleById
    );
}