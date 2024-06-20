const { Firestore } = require('@google-cloud/firestore');

async function storeData(id, data) {
  try {
    const db = new Firestore({
      projectId: process.env.PROJECT_ID,
      keyFilename: process.env.GOOGLE_APPLICATION_CREDENTIALS
    });

    const predictCollection = db.collection('predictions');

    await predictCollection.doc(id).set(data);

    console.log(`Data with ID: ${id} has been stored successfully.`);
    return { success: true };
  } catch (error) {
    console.error('Error storing data:', error);
    return { success: false, error: error.message };
  }
}

module.exports = storeData;