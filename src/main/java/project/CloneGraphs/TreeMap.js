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

    d3.selectAll("div").remove()

    const margin = {top: 40, right: 10, bottom: 10, left: 10},
        width = 960 - margin.left - margin.right,
        height = 500 - margin.top - margin.bottom,
        color = d3.scaleOrdinal().range(d3.schemeCategory10);

    const treemap = d3.treemap().size([width, height]);

    const div = d3.select("body").append("div")
        .style("position", "relative")
        .style("width", (width + margin.left + margin.right) + "px")
        .style("height", (height + margin.top + margin.bottom) + "px")
        .style("left", margin.left + "px")
        .style("top", margin.top + "px");


    const root = d3.hierarchy(data, (d) => d.children)
        .sum(function(d) {
            let size = 0;
            if(d.sizes !== undefined) {
                for(let i=0; i<d.sizes.length; i++) {
                    size += d.sizes[i];
                }
            }

            return size;
        });

    const tree = treemap(root);

    const node = div.datum(root).selectAll(".node")
        .data(tree.leaves())
        .enter().append("div")
        .attr("class", "node")
        .style("left", (d) => d.x0 + "px")
        .style("top", (d) => d.y0 + "px")
        .style("width", (d) => Math.max(0, d.x1 - d.x0 - 1) + "px")
        .style("height", (d) => Math.max(0, d.y1 - d.y0 - 1) + "px")
        .style("background", (d) => color(d.parent.data.name))
        .text((d) => d.data.name);

    d3.selectAll("input").on("change", function change() {
        const value = this.value === "count"
            ? (d) => {
                return d.size ? 1 : 0;
            }
            : (d) => {
                return d.size;
            };

        const newRoot = d3.hierarchy(data, (d) => d.children)
            .sum(value);

        node.data(treemap(newRoot).leaves())
            .transition()
            .duration(1500)
            .style("left", (d) => d.x0 + "px")
            .style("top", (d) => d.y0 + "px")
            .style("width", (d) => Math.max(0, d.x1 - d.x0 - 1) + "px")
            .style("height", (d) => Math.max(0, d.y1 - d.y0 - 1) + "px")
    });


}