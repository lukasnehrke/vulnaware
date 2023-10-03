const github = require("@actions/github");
const core = require("@actions/core");
const FormData = require("form-data");
const axios = require("axios");
const context = github.context;

async function run() {
    const token = core.getInput("token");
    const apiKey = core.getInput("apiKey");
    const server = core.getInput("server");
    const project = core.getInput("project");

    const octokit = github.getOctokit(token);
    const sbom = await octokit.request("GET /repos/{owner}/{repo}/dependency-graph/sbom", {
        ...context.repo,
        headers: {
            "X-GitHub-Api-Version": "2022-11-28",
        },
    });

    const blob = Buffer.from(JSON.stringify(sbom.data.sbom));
    const formData = new FormData();
    formData.append("file", blob, "bom.spdx.json");
    formData.append("description", "Uploaded from GitHub Actions");

    const ref = encodeURIComponent(context.ref);
    const res = await axios.post(`${server}/api/v1/${project}/analyze/main`, formData, {
        headers: {
            Accept: "application/json",
            "X-API-Key": apiKey,
        },
    });

    console.log(res);
}

run();
