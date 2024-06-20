const db = require("../models");
const Article = db.articles;
const Op = db.Sequelize.Op;

exports.createArticle = async (req, res) => {
  try {
    const { title, content, link,tag, image } = req.body;

    if (!title || !content) {
      return res.status(400).json({ message: "Title and content are required." });
    }

    const newArticle = await Article.create({
      title,
      content,
      link,
      tag,
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

exports.searchArticle = async (req, res) => {
  try {
    const title = req.body.title;
    const articles = await Article.findAll({where : {title: {[Op.like]: '%' + title + '%'}}});

    if (articles.length === 0) {
      return res.status(404).json({ message: "Article not found." });
    } else {
      res.status(200).json(articles);
    } 
  }catch (error) {
    res.status(500).send({message: 'Error retrieving article with title=' + title});
  }
};

exports.filterArticle = async (req, res) => {
  try{
    const tag = req.params.tag;
    const articles = await Article.findAll({where: {tag: tag}});

    if (articles.length === 0) {
      return res.status(404).json({ message: "Article with this tag is not found." });
    } else {
      res.status(200).json(articles);
    } 
  }catch (error) {
    res.status(500).send({message: 'Error retrieving article with tag=' + tag});
  }
};