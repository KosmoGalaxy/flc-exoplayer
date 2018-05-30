const copyDirectory = require('./copy-directory');

copyDirectory('./src/unity/Assets/FlcExoPlayer', './dist/unity/FlcExoPlayer', [
  'FlcExoPlayer.aar',
  'FlcExoPlayer.cs',
  'FlcExoPlayerUnity.aar'
]);
