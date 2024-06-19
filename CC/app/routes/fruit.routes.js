const controller = require("../controllers/fruitController");
const { verifyToken } = require("../middleware/authJwt");
const multer = require('multer');

module.exports = function(app){
    app.use(function(req, res, next) {
        res.header(
          "Access-Control-Allow-Headers",
          "Origin, Content-Type, Accept"
        );
        next();
    });

    const upload = multer({ storage: multer.memoryStorage() });
    app.post('/api/predict', verifyToken, upload.single('image'), async (req, res) => {
        if (!req.file) {
            return res.status(400).send('No image file uploaded.');
        }
    
        try {
            const result = await controller.predictClassification(req.file.buffer);
            res.json(result);
        } catch (error) {
            res.status(500).send(`Error predicting the image: ${error.message}`);
        }
    });
}