const db = require("../models");
const config = require("../config/authConfig");
const User = db.user;

const Op = db.Sequelize.Op;

const jwt = require("jsonwebtoken");
const bcrypt = require("bcryptjs");

exports.signup = async (req, res) => {
  try {
    const user = await User.create({
      username: req.body.username,
      email: req.body.email,
      password: bcrypt.hashSync(req.body.password, 8)
    });

    if (user) {
      res.send({ message: "User registered successfully!" });
    }
  } catch (error) {
    res.status(500).send({ message: error.message });
  }
};

exports.signin = async (req, res) => {
  try {
    console.log("Attempting to find user by email:", req.body.email);
    const user = await User.findOne({
      where: {
        email: req.body.email,
      },
    });

    if (!user) {
      console.log("No user found with email:", req.body.email);
      return res.status(404).send({ message: "User Not found." });
    }

    console.log("User found, checking password validity");
    const passwordIsValid = bcrypt.compareSync(req.body.password, user.password);

    if (!passwordIsValid) {
      console.log("Invalid password for user:", req.body.email);
      return res.status(401).send({
        accessToken: null,
        message: "Invalid Password!",
      });
    }

    console.log("Password is valid, signing token");
    const token = jwt.sign({ id: user.id }, config.secret, {
      expiresIn: 86400,
    });

    req.session.token = token;
    console.log("Token saved in session:", req.session.token);

    return res.status(200).send({
      message: "Sign-in successful",
      accessToken: token
    });
  } catch (error) {
    console.log("Error during sign-in:", error.message);
    res.status(500).send({ message: error.message });
  }
};

exports.signout = async (req, res) => {
  try {
    req.session = null;
    return res.status(200).send({
      message: "You've been signed out!"
    });
  } catch (err) {
    this.next(err);
  }
};