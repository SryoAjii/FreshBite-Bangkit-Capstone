const db = require("../models");
const Article = db.articles;
const Op = db.Sequelize.Op;

exports.createArticle = async (req, res) => {
  try {
    const { title, content, link, image } = req.body;

    if (!title || !content) {
      return res.status(400).json({ message: "Title and content are required." });
    }

    const newArticle = await Article.create({
      title,
      content,
      link,
      image
    });

    res.status(201).json(newArticle);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

exports.getArticles = async (req, res) => {
  try {
    const articles = await Article.findAll();
    res.status(200).json(articles);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

exports.getArticleById = async (req, res) => {
    try {
      const id = req.params.id;
      const article = await Article.findByPk(id);
  
      if (!article) {
        return res.status(404).json({ message: "Article not found." });
      }
  
      res.status(200).json(article);
    } catch (error) {
      res.status(500).json({ message: error.message });
    }
  };