// Quick script to delete the Main Pantry from the database
const sqlite3 = require('sqlite3').verbose();
const path = require('path');
const db = new sqlite3.Database(path.join(__dirname, 'src/db/init.sqlite'));

db.serialize(() => {
  // First, get the Main Pantry ID
  db.get("SELECT id FROM pantry WHERE name = 'Main Pantry' AND deletedAt IS NULL", (err, row) => {
    if (err) {
      console.error('Error finding Main Pantry:', err);
      return;
    }
    
    if (!row) {
      console.log('No Main Pantry found - already deleted or never existed');
      db.close();
      return;
    }
    
    const pantryId = row.id;
    console.log(`Found Main Pantry with ID: ${pantryId}`);
    
    // Delete all items in the Main Pantry first
    db.run("UPDATE pantry_item SET deletedAt = datetime('now') WHERE pantryId = ? AND deletedAt IS NULL", [pantryId], function(err) {
      if (err) {
        console.error('Error deleting pantry items:', err);
        return;
      }
      console.log(`Deleted ${this.changes} items from Main Pantry`);
      
      // Now delete the Main Pantry itself
      db.run("UPDATE pantry SET deletedAt = datetime('now') WHERE id = ?", [pantryId], function(err) {
        if (err) {
          console.error('Error deleting Main Pantry:', err);
          return;
        }
        console.log('✅ Main Pantry successfully deleted!');
        db.close();
      });
    });
  });
});
