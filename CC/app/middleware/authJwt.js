const jwt = require("jsonwebtoken");
const config = require("../config/authConfig.js");

verifyToken = (req, res, next) => {
  let token = req.session.token;
  
  console.log("Token from session:", token); 

  if (!token) {
    return res.status(403).send({
      message: "No token provided!",
    });
  }

  jwt.verify(token, config.secret, (err, decoded) => {
    if (err) {
      console.log("Token verification error:", err); 
      return res.status(401).send({
        message: "Unauthorized!",
      });
    }
    console.log("Decoded token ID:", decoded.id); 
    req.user = decoded;
    next();
  });
};

const authJwt = {
  verifyToken,
};
module.exports = authJwt;