const express = require("express");
const cors = require("cors");
const cookieSession = require("cookie-session");

const app = express();
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(
  cookieSession({
    name: "bezkoder-session",
    keys: ["COOKIE_SECRET"], 
    httpOnly: true,
  })
);


require('./app/routes/auth.routes')(app);
require('./app/routes/user.routes')(app);
require('./app/routes/article.routes')(app);

const db = require("./app/models");
db.sequelize.sync({force: false}).then(() => {
  console.log('Drop and Resync Db');
});

app.get("/", (req, res) => {
  res.json({ message: "Welcome to FreshBite application." });
});

const PORT = process.env.PORT;
app.listen(PORT, async () => {
  console.log(`Server is running on port ${PORT}.`);
});