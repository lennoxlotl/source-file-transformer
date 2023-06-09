package de.lennox.srcvt

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required

/**
 * Parses the input arguments and then, if valid runs the [SourceVersionTransformerApplication]
 *
 * @since 1.0.0
 * @author Lennox
 */
fun main(args: Array<String>) {
    val parser = ArgParser("source-file-transformer")
    val input by parser.option(ArgType.String, shortName = "i", description = "The input file").required()
    val output by parser.option(ArgType.String, shortName = "o", description = "The output file").required()
    val targetVersion by parser.option(ArgType.Int, shortName = "t", description = "The target source version").required()
    parser.parse(args)
    SourceVersionTransformerApplication(input, output, targetVersion).run()
}