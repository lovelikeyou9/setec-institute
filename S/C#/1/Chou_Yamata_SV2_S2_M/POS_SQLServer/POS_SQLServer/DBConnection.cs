using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Data.SqlClient;
using POS_System_Product;

namespace POS_SQLServer
{
    class DBConnection
    {
        public static SqlConnection DataCon { get; set; }


        public static string ConnStr { get; private set; }


        public static List<Product> Products
        { get {
            List<Product> products = new List<Product>();

                string sql = "select * from tblProduct";
                SqlCommand cmd = new SqlCommand(sql, DataCon);
                SqlDataReader reader = cmd.ExecuteReader();

                while (reader.Read())
                {
                    long id = reader.GetInt64(0);
                    string pName = reader.GetString(1);
                    double price = reader.GetSqlMoney(2).ToDouble();
                    int qty = reader.GetInt32(3);
                    byte[] photo = (byte[])reader[4];
                    long cId = reader.GetInt64(5);
                    Product product = new Product(id, pName, price, qty, photo, cId);
                    products.Add(product);
                }

                reader.Close();
                cmd.Dispose();
                return products;

            } }


        // Windows Authentication
        public static void ConnectionString(string server, string dbName)
        {
            ConnStr = $"Server={server};Database={dbName};Trusted_Connection=True;TrustServerCertificate=True;";
            DataCon = new SqlConnection(ConnStr);
            DataCon.Open();
        }

        // SQL Server Authentication
        public static void ConnectionString(string server, string dbName, string user, string pass)
        {
            ConnStr = $"Server={server};Database={dbName};User Id={user};Password={pass};TrustServerCertificate=True;";
            DataCon = new SqlConnection(ConnStr);
            DataCon.Open();
        }




    }
}
