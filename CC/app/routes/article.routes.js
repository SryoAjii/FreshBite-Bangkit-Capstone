const controller = require("../controllers/articleController");
const { verifyToken } = require("../middleware/authJwt");

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
        verifyToken,
        controller.createArticle
    );

    app.post(
        "/api/articles/search",
        verifyToken,
        controller.searchArticle
    );
    
    app.get(
        "/api/articles",
        verifyToken,
        controller.getArticles
    );

    app.get(
        "/api/articles/:id",
        verifyToken, 
        controller.getArticleById
    );

    app.get(
        "/api/articles/tag/:tag",
        verifyToken,
        controller.filterArticle
    );
}