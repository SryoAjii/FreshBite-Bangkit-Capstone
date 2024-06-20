const tf = require('@tensorflow/tfjs-node');
const loadModel = require('../services/loadModel');
const storeData = require('../services/storedData'); 
const crypto = require('crypto');

async function predictClassification(imageBuffer) {
  try {
    const model = await loadModel();
    if (!model) {
      throw new Error("Model failed to load");
    }

    const tensor = tf.node.decodeImage(imageBuffer)
      .resizeNearestNeighbor([224, 224])
      .expandDims()
      .toFloat().div(tf.scalar(255.0));  

    const prediction = model.predict(tensor);
    const score = await prediction.data();
    const confidenceScore = Math.max(...score) * 100;

    const classes = ['freshapple', 'freshbanana', 'freshoranges', 'rottenapples', 'rottenbananas', 'rottenoranges'];
    const classResult = tf.argMax(prediction, 1).dataSync()[0];
    const label = classes[classResult];

    let explanation, suggestion;
    if (label.includes('fresh')) {
      explanation = "Buah ini masih segar dan baik untuk dikonsumsi.";
      suggestion = "Disarankan untuk segera dikonsumsi sementara masih segar atau disimpan dengan baik.";
    } else {
      explanation = "Buah ini sudah membusuk dan tidak layak konsumsi.";
      suggestion = "Disarankan untuk membuang buah ini dan memeriksa buah lain yang sehat.";
    }

    // const dataToStore = {
    //   label,
    //   confidenceScore,
    //   explanation,
    //   suggestion,
    //   timestamp: new Date()
    // };

    // const documentId = crypto.randomBytes(16).toString('hex');

    // const storeResult = await storeData(documentId, dataToStore);
    // if (!storeResult.success) {
    //   throw new Error(`Failed to store prediction data: ${storeResult.error}`);
    // }

    return { confidenceScore, label, explanation, suggestion };
  } catch (error) {
    throw new Error(`Error processing input: ${error.message}`);
  }
}

module.exports = {
  predictClassification
};