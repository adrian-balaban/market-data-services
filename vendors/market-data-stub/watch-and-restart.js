
const chokidar = require('chokidar');
const { exec } = require('child_process');
const path = require('path');

// Configuration
const WATCH_DIR = path.resolve(__dirname, './'); // Watch directory
const COMPOSE_FILE = path.resolve(__dirname, '../../infra/local/docker-compose-stubs.yml'); // path to docker compose
const SERVICE_NAME = 'fx-market-stub';

console.log(`Watching for changes in ${WATCH_DIR}, excluding node_modules...`);

function runCommand(command) {
    const platformCommand = process.platform === 'win32' ? `cmd.exe /c ${command}` : command;
    exec(platformCommand, (err, stdout, stderr) => {
        if (err) {
            console.error(`Error: ${stderr}`);
        } else {
            console.log(stdout);
        }
    });
}

chokidar
    .watch(WATCH_DIR, {
        persistent: true,
        ignored: /node_modules/,
        ignoreInitial: true,
    })
    .on('all', (event, filePath) => {
        console.log(`Detected ${event} in file: ${filePath}`);
        console.log('Restarting container...');

        // Restart container
        const command = `podman compose -f ${COMPOSE_FILE} restart ${SERVICE_NAME}`;
        runCommand(command);
    });