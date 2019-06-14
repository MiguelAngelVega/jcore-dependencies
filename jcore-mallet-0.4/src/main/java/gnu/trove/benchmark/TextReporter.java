///////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2001, Eric D. Friedman All Rights Reserved.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////

package gnu.trove.benchmark;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;

class TextReporter implements Reporter {
    PrintWriter out;

    TextReporter() {
        this(new PrintWriter(new OutputStreamWriter(System.out),true));
    }

    TextReporter(PrintWriter out) {
        this.out = out;
    }

    public void report(Result result) {
        out.print(result);
        out.println("--------------------------------");
    }

    public void start() {
        out.println("--------------------------------");
        out.println("GNU Trove Benchmark suite");
        out.println("--------------------------------");
        for (int i = 0; i < ENV_PROPS.length; i++) {
            String key = ENV_PROPS[i];
            out.println(key + "=" + System.getProperty(key));
        }
        out.println("--------------------------------");
    }

    public void finish() {
        out.println("done");
    }
}
