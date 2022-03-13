
function printTreeMap(
    displayDirNames,
    displayNodes,
    displayFileNames,
    type1,
    type2,
    type3,
    moreLess,
    ccSize
) {

    alert("TREE MAP");

    const cloneGraph = document.getElementById("chart");
    cloneGraph.textContent = '';

    let format = d3.format(",d")

    color = d3.scaleSequential([8, 0], d3.interpolateMagma)

    let width = 954
    let height = 1060

    let treemap = data => d3.treemap()
        .size([width, height])
        .paddingOuter(3)
        .paddingTop(19)
        .paddingInner(1)
        .round(true)
        (d3.hierarchy(data)
            .sum(d => d.value)
            .sort((a, b) => b.value - a.value))

    const root = treemap(data);

    const svg = d3.create("svg")
        .attr("viewBox", [0, 0, width, height])
        .style("font", "10px sans-serif");

    const shadow = uid("shadow");

    svg.append("filter")
        .attr("id", shadow.id)
        .append("feDropShadow")
        .attr("flood-opacity", 0.3)
        .attr("dx", 0)
        .attr("stdDeviation", 3);

    const node = svg.selectAll("g")
        .data(d3.group(root, d => d.height))
        .join("g")
        .attr("filter", shadow)
        .selectAll("g")
        .data(d => d[1])
        .join("g")
        .attr("transform", d => `translate(${d.x0},${d.y0})`);

    node.append("title")
        .text(d => `${d.ancestors().reverse().map(d => d.data.name).join("/")}\n${format(d.value)}`);

    node.append("rect")
        .attr("id", d => (d.nodeUid = uid("node")).id)
        .attr("fill", d => color(d.height))
        .attr("width", d => d.x1 - d.x0)
        .attr("height", d => d.y1 - d.y0);

    node.append("clipPath")
        .attr("id", d => (d.clipUid = uid("clip")).id)
        .append("use")
        .attr("xlink:href", d => d.nodeUid.href);

    // node.append("text")
    //     .attr("clip-path", d => d.clipUid)
    //     .selectAll("tspan")
    //     .data(d => d.data.name.split(/(?=[A-Z][^A-Z])/g).concat(format(d.value)))
    //     .join("tspan")
    //     .attr("fill-opacity", (d, i, nodes) => i === nodes.length - 1 ? 0.7 : null)
    //     .text(d => d);

    node.filter(d => d.children).selectAll("tspan")
        .attr("dx", 3)
        .attr("y", 13);

    node.filter(d => !d.children).selectAll("tspan")
        .attr("x", 3)
        .attr("y", (d, i, nodes) => `${(i === nodes.length - 1) * 0.3 + 1.1 + i * 0.9}em`);

}

function uid(prefix) {
    var d = new Date().getTime();
    d += (parseInt(Math.random() * 100)).toString();
    if (undefined === prefix) {
        prefix = 'uid-';
    }
    d = prefix + d;
    return d;
};